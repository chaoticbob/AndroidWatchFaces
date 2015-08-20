package org.hai.fx;

import android.util.Log;

import org.hai.gl.GlslProg;
import org.hai.grfx.Camera;
import org.hai.grfx.Rect;
import org.hai.grfx.es2.LineMesh3D;
import org.hai.grfx.es2.PointMesh3D;

import java.util.ArrayList;

/**
 * Created by hai on 4/11/15.
 */
public class SpringMesh {

    private int resX                = 0;
    private int resY                = 0;
    private Rect bounds             = null;
    private int numPointsX          = 0;
    private int numPointsY          = 0;

    private float cellSizeX         = 0;
    private float cellSizeY         = 0;

    private int[] springs           = null;
    private float[] springLengths   = null;
    private float[] pos             = null;
    private float[] prevPos         = null;
    private float[] accel           = null;
    private float[] response        = null;

    private float[] texCoords       = null;

    private GlslProg shader         = null;
    private LineMesh3D lineMesh     = null;
    private PointMesh3D pointMesh   = null;

    public SpringMesh(int nx, int ny, Rect abounds) {
        this.resX        = nx;
        this.resY        = ny;
        this.bounds      = new Rect(abounds);
        this.numPointsX  = nx + 1;
        this.numPointsY  = ny + 1;

        /***********************************************************************
         * Setup                                                               *
         ***********************************************************************/
        ArrayList<Integer> initialSprings       = new ArrayList<>();
        ArrayList<Float> initialSpringLengths   = new ArrayList<>();
        //
        int n = this.numPointsX*this.numPointsY;
        float[] initialPositions    = new float[3*n];
        float[] initialTexCoords    = new float[2*n];
        this.prevPos                = new float[3*n];
        this.accel                  = new float[3*n];
        this.response               = new float[n];

        this.texCoords              = new float[2*n];

        // Poisitions/accel/response
        float dx = this.bounds.getWidth()/(this.numPointsX - 1);
        float dy = this.bounds.getHeight()/(this.numPointsY - 1);
        Log.i("watchfacetest", "dx: " + dx + ", dy: " + dy);

        this.cellSizeX = dx;
        this.cellSizeY = dy;

        for( int j = 0; j < this.numPointsY; ++j ) {
            for( int i = 0; i < this.numPointsX; ++i ) {
                int idx  = j*this.numPointsX + i;
                int xidx = 3*idx + 0;
                int yidx = 3*idx + 1;
                int zidx = 3*idx + 2;

                float x = this.bounds.x1 + i*dx;
                float y = this.bounds.y1 + j*dy;
                float z = 0.0f;

                initialPositions[xidx] = x;
                initialPositions[yidx] = y;
                initialPositions[zidx] = z;
                this.prevPos[xidx] = x;
                this.prevPos[yidx] = y;
                this.prevPos[zidx] = z;

                this.accel[xidx] = 0.0f;
                this.accel[yidx] = 0.0f;
                this.accel[zidx] = 0.0f;
                //
                float response = 1.0f;
                if( 0 == i || 0 == j || (this.numPointsX - 1) == i || (this.numPointsY - 1) == j ) {
                    response = 0.0f;
                }
                this.response[idx] = response;

                float u = (float)i/(float)(this.numPointsX - 1);
                float v = (float)j/(float)(this.numPointsY - 1);
                initialTexCoords[2*idx + 0] = u;
                initialTexCoords[2*idx + 1] = v;
                this.texCoords[2*idx + 0] = u;
                this.texCoords[2*idx + 1] = v;
            }
        }

        // horizontal lines
        ArrayList<Integer> hlines = new ArrayList<>();
        for( int j = 0; j < this.numPointsY; ++j ) {
            for( int i = 0; i < (this.numPointsX - 1); ++i ) {
                int index0 = j*this.numPointsX + (i + 0);
                int index1 = j*this.numPointsX + (i + 1);
                hlines.add(index0);
                hlines.add(index1);
                //
                initialSprings.add(index0);
                initialSprings.add(index1);
                initialSpringLengths.add(0.0f);
            }
        }

        // vertical lines
        ArrayList<Integer> vlines = new ArrayList<>();
        for( int j = 0; j < (this.numPointsY - 1); ++j ) {
            for( int i = 0; i < this.numPointsX; ++i ) {
                int index0 = (j + 0)*this.numPointsX + i;
                int index1 = (j + 1)*this.numPointsX + i;
                vlines.add(index0);
                vlines.add(index1);
                initialSprings.add(index0);
                initialSprings.add(index1);
                initialSpringLengths.add(0.0f);
            }
        }

        // diagonal
        ArrayList<Integer> dlines = new ArrayList<>();
        for( int j = 0; j < (this.numPointsY - 1); ++j ) {
            for( int i = 0; i < (this.numPointsX - 1); ++i ) {
                int index0 = (j + 0)*this.numPointsX + (i + 0);
                int index1 = (j + 1)*this.numPointsX + (i + 1);
                dlines.add(index0);
                dlines.add(index1);
                initialSprings.add(index0);
                initialSprings.add(index1);
                initialSpringLengths.add(0.0f);

                index0 = (j + 0)*this.numPointsX + (i + 1);
                index1 = (j + 1)*this.numPointsX + (i + 0);
                dlines.add(index0);
                dlines.add(index1);
                initialSprings.add(index0);
                initialSprings.add(index1);
                initialSpringLengths.add(0.0f);
            }
        }

        ArrayList<Integer> initialIndices = new ArrayList<>();
        initialIndices.addAll(hlines);
        initialIndices.addAll(vlines);
        initialIndices.addAll(dlines);

        // Build arrays
        int[] initialIndicesArray = new int[initialIndices.size()];
        for(int i = 0; i < initialIndices.size(); ++i) {
            initialIndicesArray[i] = initialIndices.get(i);
        }
        float[] initialSpringLengthsArray = new float[initialSpringLengths.size()];
        for(int i = 0; i < initialSpringLengths.size(); ++i) {
            initialSpringLengthsArray[i] = initialSpringLengths.get(i);
        }


        // Create meshes
        this.lineMesh = new LineMesh3D();
        this.lineMesh.bufferIndices(initialIndicesArray);
        this.lineMesh.bufferPositions(initialPositions);
        this.lineMesh.bufferTexCoords(initialTexCoords);
        this.lineMesh.getPositions().setDynamicDraw();
        //this.lineMesh.update();
        this.pointMesh = new PointMesh3D();
        this.pointMesh.bufferPositions(initialPositions);
        this.pointMesh.bufferTexCoords(initialTexCoords);
        this.pointMesh.getPositions().setDynamicDraw();
        //this.pointMesh.update();

        // Wire up some params
        this.pos = initialPositions;

        this.springs = initialIndicesArray;
        this.springLengths = initialSpringLengthsArray;
        // Calculate all the rest lengths
        for( int i = 0; i < this.springs.length/2; ++i ) {
            int springIdx0 = 2*i + 0;
            int springIdx1 = 2*i + 1;

            int pidx0 = 3*this.springs[springIdx0];
            int pidx1 = 3*this.springs[springIdx1];

            // P0
            float P0x = this.pos[pidx0 + 0];
            float P0y = this.pos[pidx0 + 1];
            float P0z = this.pos[pidx0 + 2];

            // P1
            float P1x = this.pos[pidx1 + 0];
            float P1y = this.pos[pidx1 + 1];
            float P1z = this.pos[pidx1 + 2];

            // dv
            float dvx = P1x - P0x;
            float dvy = P1y - P0y;
            float dvz = P1z - P0z;

            float restLen = (float)Math.sqrt( dvx*dvx + dvy*dvy + dvz*dvz );
            this.springLengths[i] = restLen;
        }

        // Copy to prev pos
        for( int i = 0; i < this.pos.length; ++i ) {
            this.prevPos[i] = this.pos[i];
        }

        float s = 150000.0f;
        for( int j = 1; j < (this.numPointsY - 1); ++j ) {
            for( int i = 1; i < (this.numPointsX - 1); ++i ) {
                int idx = j*this.numPointsX + i;
                this.accel[3*idx + 0] += s*(Math.random() - 0.5f);
                this.accel[3*idx + 1] += s*(Math.random() - 0.5f);
                this.accel[3*idx + 2] += s*(Math.random() - 0.5f);
            }
        }

        Log.i("watchfacetest", "Num springs: " + this.springs.length/2);
    }

    public float getCellSizeX() {
        return this.cellSizeX;
    }

    public float getCellSizeY() {
        return this.cellSizeY;
    }

    public void setOriginOffset(float x, float y) {
        this.lineMesh.getTransform().setOriginOffset(x, y, 0.0f);
    }

    public float[] getTexCoords() {
        return this.texCoords;
    }

    public GlslProg getShader() {
        return this.shader;
    }

    public void setShader(GlslProg shaderProg) {
        this.shader = shaderProg;
        this.lineMesh.setShader(this.shader);
        this.pointMesh.setShader(this.shader);
    }

    public void applyCircleForce(float[] points, float radius, float strength) {
        float radiusSq = radius*radius;
        for( int ip = 0; ip < points.length/2; ++ip ) {
            float x = points[2*ip + 0];
            float y = points[2*ip + 1];
            /// NOT USED: var z = 0;

            for( int i = 0; i < this.pos.length/3; ++i ) {
                int idx  = 3*i;
                int xidx = idx + 0;
                int yidx = idx + 1;
                int zidx = idx + 2;

                float dx = this.pos[xidx] - x;
                float dy = this.pos[yidx] - y;
                /// NOT USED: var dz = this.pos[zidx] - z;

                float distSq = dx*dx + dy*dy;
                if( distSq < radiusSq ) {
                    // Cap the distance so that it doesn't fall below 1.0
                    distSq = (float)Math.max( distSq, 1.0 );
                    // Normalize the direction vector
                    float s = 1.0f/(float)Math.sqrt( distSq );
                    float nx = dx*s;
                    float ny = dy*s;
                    float nz = 0.0f;
                    // Calculate force
                    float falloff = (1.0f - distSq/radiusSq);
                    //falloff = falloff*falloff;
                    s = falloff*strength;
                    float vx = nx*s;
                    float vy = ny*s;
                    float vz = -2.0f*s;
                    // Apply force to acceleration - falling off to the edge.
                    this.accel[xidx] += vx;
                    this.accel[yidx] += vy;
                    this.accel[zidx] += vz;
                }
            }
        }
    }

    public void update() {
        float conserve = 0.98f;
        int niter = 2;
        float dt = 1.0f/60.0f/(float)niter;

        for( int iter = 0; iter < niter; ++iter ) {
            // Calculate spring force
            for( int i = 0; i < this.springs.length/2; ++i ) {
                int springIdx0 = 2*i + 0;
                int springIdx1 = 2*i + 1;

                int pidx0 = 3*this.springs[springIdx0];
                int pidx1 = 3*this.springs[springIdx1];

                // P0
                float P0x = this.pos[pidx0 + 0];
                float P0y = this.pos[pidx0 + 1];
                float P0z = this.pos[pidx0 + 2];

                // P1
                float P1x = this.pos[pidx1 + 0];
                float P1y = this.pos[pidx1 + 1];
                float P1z = this.pos[pidx1 + 2];

                // dv
                float dx = P1x - P0x;
                float dy = P1y - P0y;
                float dz = P1z - P0z;

                float restLen = this.springLengths[i];
                float curLen = (float)Math.sqrt( dx*dx + dy*dy + dz*dz );
                float ratio = (restLen - curLen)/restLen*50.0f;

                dx = ratio*dx;
                dy = ratio*dy;
                dz = ratio*dz;

                this.accel[pidx0 + 0] += -dx;
                this.accel[pidx0 + 1] += -dy;
                this.accel[pidx0 + 2] += -dz;
                this.accel[pidx1 + 0] +=  dx;
                this.accel[pidx1 + 1] +=  dy;
                this.accel[pidx1 + 2] +=  dz;
            }

            // apply velocity/accel
            for( int i = 0; i < this.pos.length/3; ++i ) {
                int idx  = 3*i;
                int xidx = idx + 0;
                int yidx = idx + 1;
                int zidx = idx + 2;

                float vx = this.pos[xidx] - this.prevPos[xidx];
                float vy = this.pos[yidx] - this.prevPos[yidx];
                float vz = this.pos[zidx] - this.prevPos[zidx];

                this.prevPos[xidx] = this.pos[xidx];
                this.prevPos[yidx] = this.pos[yidx];
                this.prevPos[zidx] = this.pos[zidx];

                float response = this.response[i];

                this.pos[xidx] += 0.5*dt*vx*response;
                this.pos[yidx] += 0.5*dt*vy*response;
                this.pos[zidx] += 0.5*dt*vz*response;

                this.pos[xidx] += dt*dt*this.accel[xidx]*response;
                this.pos[yidx] += dt*dt*this.accel[yidx]*response;
                this.pos[zidx] += dt*dt*this.accel[zidx]*response;
            }

            // Dampen accel
            for( int i = 0; i < this.accel.length; ++i ) {
                this.accel[i] *= conserve;
            }
        }

        // Update lineMesh positions and texCoords
        this.lineMesh.bufferPositions(this.pos);
        this.lineMesh.bufferTexCoords(this.texCoords);
        // Update pointMesh positions
        this.pointMesh.bufferPositions(this.pos);
    }

    public void draw(Camera cam) {
        this.lineMesh.drawBegin();
        this.lineMesh.getShader().uniform("xform", this.lineMesh.getTransform().getMatrix());
        //this.lineMesh.getShader().uniform("color", 1.0f, 1.0f, 0.0f);
        this.lineMesh.draw(cam);
        this.lineMesh.drawEnd();

        /*
        this.pointMesh.drawBegin();
        this.pointMesh.getShader().uniform("xform", this.pointMesh.getTransform().getMatrix());
        //this.pointMesh.getShader().uniform("color", 1.0f, 0.0f, 0.0f);
        this.pointMesh.draw(cam);
        this.pointMesh.drawEnd();
        */
    }
}