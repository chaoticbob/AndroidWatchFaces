package org.hai.fx;

import org.hai.grfx.Camera;
import org.hai.grfx.Rect;

public class Fluids2D {

    public class Force {
        public float x;
        public float y;
        public float dx;
        public float dy;

        public Force() {}

//        Force(float x, float y, float dx, float dy) {
//            this.x = x;
//            this.y = y;
//            this.dx = dx;
//            this.dy = dy;
//        }
    }

    private int resX                = 0;
    private int resY                = 0;
    private Rect bounds             = null;

    private float cellSizeX         = 0.0f;
    private float cellSizeY         = 0.0f;
    private float halfDivCellSizeX  = 0.0f;
    private float halfDivCellSizeY  = 0.0f;

    private float[] xvel0           = null;
    private float[] yvel0           = null;
    private float[] xvel1           = null;
    private float[] yvel1           = null;
    private float[] div             = null;
    private float[] prs             = null;
    private float[] curl            = null;
    private float[] curlLength      = null;

    private boolean enableDensity   = true;
    private float[] den0            = null;
    private float[] den1            = null;

    private boolean wrapBoundary    = false;

    private float dt                = 0.1f;

    private float velDiss           = 0.99999f;
    private float denDiss           = 0.99000f;

    private float velVisc           = 0.1f;
    private float denVisc           = 0.1f;

    private float vorticityScale    = 0.25f;

    public Fluids2D(int nx, int ny, Rect bounds) {
        this.resX = nx;
        this.resY = ny;
        this.bounds = bounds;

        this.cellSizeX = this.bounds.getWidth()/this.resX;
        this.cellSizeY = this.bounds.getHeight()/this.resY;
        this.halfDivCellSizeX = 0.5f/this.cellSizeX;
        this.halfDivCellSizeY = 0.5f/this.cellSizeY;

        int nsize       = this.resX*this.resY;
        this.xvel0      = new float[nsize];
        this.yvel0      = new float[nsize];
        this.xvel1      = new float[nsize];
        this.yvel1      = new float[nsize];
        this.div        = new float[nsize];
        this.prs        = new float[nsize];
        this.curl       = new float[nsize];
        this.curlLength = new float[nsize];

        this.enableDensity = true;
        this.den0 = new float[nsize];
        this.den1 = new float[nsize];

        this.wrapBoundary = false;

        this.dt = 0.1f;

        this.velDiss = 0.99999f;
        this.denDiss = 0.99000f;

        this.velVisc = 0.1f;
        this.denVisc = 0.1f;

        this.vorticityScale = 0.25f;

        int iStart = 1;
        int iEnd   = iStart + this.resX;
        int jStart = 1;
        int jEnd   = jStart + this.resY;
        //
        float velX      = 0.0f;
        float velY      = -50.0f;
        float maxDist   = 0.15f*(Math.min( this.resX, this.resY ));
        float maxDistSq = maxDist*maxDist;

        for( int i = 0; i < this.xvel0.length; ++i ) {
            this.xvel0[i] = 0.0f;
            this.yvel0[i] = 0.0f;
            this.xvel1[i] = 0.0f;
            this.yvel1[i] = 0.0f;
        }

        for( int i = 0; i < this.den0.length; ++i ) {
            this.den0[i] = 0.0f;
            this.den1[i] = 0.0f;
        }

        float centerX = 0.5f*this.resX;
        float centerY = 0.5f*this.resY;
        //
        for( int j = 0; j < this.resY; ++j ) {
            for( int i = 0; i < this.resX; ++i ) {
                float dx = i - centerX;
                float dy = j - centerY;
                float distSq = dx*dx + dy*dy;
                if( distSq < maxDistSq ) {
                    //float falloff = 1.0f - (distSq/maxDistSq);
                    //falloff = falloff*falloff;
                    //
                    int x = i;
                    int y = j + (int)Math.floor(maxDist);
                    int idx = y*this.resX + x;
                    this.xvel0[idx] = velX;
                    this.yvel0[idx] = velY;
                    //
                    this.den0[idx] = 5.0f;
                }
            }
        }
    }

    public float[] getDensity() {
        return this.den1;
    }


    class SplatPoints {
        int x;
        int y;
        float s;

        public SplatPoints() {}
    }

    static SplatPoints[] sPoints = null;

    private void allocateSplatPoints() {
        if(null == sPoints) {
            sPoints = new SplatPoints[4];
            for(int i = 0; i < sPoints.length; ++i) {
                sPoints[i] = new SplatPoints();
            }
        }
    }

    public void splatVelocity(Force[] forces, float strength) {
        allocateSplatPoints();

        for( int i = 0; i < forces.length; ++i ) {
            Force f = forces[i];

            int w = this.resX - 1;
            int h = this.resY - 1;

            float x = f.x*w;
            float y = f.y*h;
            float dx = f.dx*w;
            float dy = f.dy*h;
            x = Math.max( 0.0f, Math.min( w, x ) );
            y = Math.max( 0.0f, Math.min( h, y ) );

            //int x0 = (int)Math.floor( x );
            //int y0 = (int)Math.floor( y );
            int x0 = (int)x;
            int y0 = (int)y;
            int x1 = x0 + 1;
            int y1 = y0 + 1;

            float a1 = x - (float)x0;
            float b1 = y - (float)y0;
            float a0 = 1.0f - a1;
            float b0 = 1.0f - b1;

            sPoints[0].x = x0; sPoints[0].y = y0; sPoints[0].s = a0*b0;
            sPoints[1].x = x1; sPoints[1].y = y0; sPoints[1].s = a1*b0;
            sPoints[2].x = x0; sPoints[2].y = y1; sPoints[2].s = a0*b1;
            sPoints[3].x = x1; sPoints[3].y = y1; sPoints[3].s = a1*b1;

            float scale = 250.0f*strength;
            for(int k = 0; k < sPoints.length; ++k) {
                SplatPoints p = sPoints[k];
                int idx = p.y*this.resX + p.x;
                float valx = p.s*dx*scale;
                float valy = p.s*dy*scale;
                this.xvel0[idx] += valx;
                this.yvel0[idx] += valy;
            }
        }
    }

    public void splatDensity(Force[] forces, float strength) {
        allocateSplatPoints();

        for( int i = 0; i < forces.length; ++i ) {
            Force f = forces[i];

            int w = this.resX - 1;
            int h = this.resY - 1;

            float x = f.x*w;
            float y = f.y*h;
            x = Math.max( 0.0f, Math.min( w, x ) );
            y = Math.max( 0.0f, Math.min( h, y ) );

            //int x0 = (int)Math.floor( x );
            //int y0 = (int)Math.floor( y );
            int x0 = (int)x;
            int y0 = (int)y;
            int x1 = x0 + 1;
            int y1 = y0 + 1;

            float a1 = x - (float)x0;
            float b1 = y - (float)y0;
            float a0 = 1.0f - a1;
            float b0 = 1.0f - b1;

            sPoints[0].x = x0; sPoints[0].y = y0; sPoints[0].s = a0*b0;
            sPoints[1].x = x1; sPoints[1].y = y0; sPoints[1].s = a1*b0;
            sPoints[2].x = x0; sPoints[2].y = y1; sPoints[2].s = a0*b1;
            sPoints[3].x = x1; sPoints[3].y = y1; sPoints[3].s = a1*b1;

            float scale = 32.0f*strength;
            for(int k = 0; k < sPoints.length; ++k) {
                SplatPoints p = sPoints[k];
                int idx = p.y*this.resX + p.x;
                float val = p.s*scale;
                this.den0[idx] += val;
            }
        }
    }

    public void setZeroBoundary(float[] field) {
        int resX = this.resX;
        int resY = this.resY;

        int m = resX - 1;
        for( int j = 0; j < resY; ++j ) {
            int idx0 = j*resX + 0;
            int idx1 = j*resX + m;
            field[idx0] = 0.0f;
            field[idx1] = 0.0f;
        }

        int n = resY - 1;
        for( int i = 0; i < resX; ++i ) {
            int idx0 = 0*resX + i;
            int idx1 = n*resX + i;
            field[idx0] = 0.0f;
            field[idx1] = 0.0f;
        }
    }

    public void setWrapBoundary(float[] field) {
        int resX = this.resX;
        int resY = this.resY;

        int m = resX - 1;
        int x0 = 1;
        int x1 = m - 1;
        for( int j = 0; j < resY; ++j ) {
            int idx0 = j*resX + 0;
            int idx1 = j*resX + m;
            int wdx0 = j*resX + x1;
            int wdx1 = j*resX + x0;
            field[idx0] = field[wdx0];
            field[idx1] = field[wdx1];
        }

        int n = resY - 1;
        int y0 = 1;
        int y1 = n - 1;
        for( int i = 0; i < resX; ++i ) {
            int idx0 = 0*resX + i;
            int idx1 = n*resX + i;
            int wdx0 = y1*resX + i;
            int wdx1 = y0*resX + i;
            field[idx0] = field[wdx0];
            field[idx1] = field[wdx1];
        }

        field[0*resX + 0] = field[y1*resX + x1];
        field[0*resX + m] = field[y1*resX + x0];
        field[n*resX + 0] = field[y0*resX + x1];
        field[n*resX + m] = field[y0*resX + x0];
    }

    public void setBoundary(float[] field) {
        if( this.wrapBoundary ) {
            this.setWrapBoundary( field );
        }
        else {
            this.setZeroBoundary( field );
        }
    }

    public void advectAndDiffuse( float diss, float visc, float dt, float[] xvel, float[] yvel, float[] src, float[] dst ) {
        int iStart = 1;
        int iEnd   = this.resX - 1;
        int jStart = 1;
        int jEnd   = this.resY - 1;

        float xMin = 0.5f;
        float xMax = (float)this.resX - 1.5f;
        float yMin = 0.5f;
        float yMax = (float)this.resY - 1.5f;

        float alpha   = this.cellSizeX*this.cellSizeY/(visc*dt);
        float beta    = 4.0f + alpha;
        float invBeta = 1.0f/beta;

        int resX = this.resX;
        int resY = this.resY;

        for( int j = jStart; j < jEnd; ++j ) {
            for( int i = iStart; i < iEnd; ++i ) {
                int idx = j*resX + i;

                float velx = xvel[idx];
                float vely = yvel[idx];
                float dx = dt*velx;
                float dy = dt*vely;
                float iPrev = i - dx;
                float jPrev = j - dy;
                iPrev = Math.max( xMin, Math.min( xMax, iPrev ) );
                jPrev = Math.max( yMin, Math.min( yMax, jPrev ) );

                // Calculate bilinear values
                //int x0 = (int)Math.floor( iPrev );
                //int y0 = (int)Math.floor( jPrev );
                int x0 = (int)iPrev;
                int y0 = (int)jPrev;
                int x1 = x0 + 1;
                int y1 = y0 + 1;
                float a1 = iPrev - x0;
                float b1 = jPrev - y0;
                float a0 = 1.0f - a1;
                float b0 = 1.0f - b1;

                int advIdx00 = y0*resX + x0;
                int advIdx10 = y0*resX + x1;
                int advIdx01 = y1*resX + x0;
                int advIdx11 = y1*resX + x1;
                //
                float v00 = src[advIdx00];
                float v10 = src[advIdx10];
                float v01 = src[advIdx01];
                float v11 = src[advIdx11];
                float advected = (b0*(a0*v00 + a1*v10) +
                                  b1*(a0*v01 + a1*v11));

                int difIdxL = (j + 0)*resX + (i - 1);
                int difIdxR = (j + 0)*resX + (i + 1);
                int difIdxB = (j - 1)*resX + (i + 0);
                int difIdxT = (j + 1)*resX + (i + 0);
                int difIdxC = (j*resX + i);
                //
                float xL = src[difIdxL];
                float xR = src[difIdxR];
                float xB = src[difIdxB];
                float xT = src[difIdxT];
                float bC = src[difIdxC];
                float diffused = (xL + xR + xB + xT + alpha*bC)*invBeta;

                float val = diss*(0.75f*advected + 0.25f*diffused);
                dst[idx] = val;
            }
        }
    }

    public void computeDivergence( float[] xvel, float[] yvel, float[] outDiv ) {
        int iStart = 1;
        int iEnd   = this.resX - 1;
        int jStart = 1;
        int jEnd   = this.resY - 1;

        int resX = this.resX;
        int resY = this.resY;

        for( int i = 0; i < outDiv.length; ++i ) {
            outDiv[i] = 0.0f;
        }

        for( int j = jStart; j < jEnd; ++j ) {
            for(int i = iStart; i < iEnd; ++i ) {
                int idx = j*resX + i;

                int idxL = (j + 0)*resX + (i - 1);
                int idxR = (j + 0)*resX + (i + 1);
                int idxB = (j - 1)*resX + (i + 0);
                int idxT = (j + 1)*resX + (i + 0);

                float diffX = xvel[idxR] - xvel[idxL];
                float diffY = yvel[idxT] - yvel[idxB];

                float val = this.halfDivCellSizeX*diffX + this.halfDivCellSizeY*diffY;
                outDiv[idx] = val;
            }
        }
    }

    public void jacobi( float alpha, float beta, float[] xMat, float[] bMat, float[] outMat, int niters ) {
        int iStart = 1;
        int iEnd   = this.resX - 1;
        int jStart = 1;
        int jEnd   = this.resY - 1;

        int resX = this.resX;
        int resY = this.resY;

        float invBeta = 1.0f/beta;
        for( int iter = 0; iter < niters; ++iter ) {
            for( int j = jStart; j < jEnd; ++j ) {
                for( int i = iStart; i < iEnd; ++i ) {
                    int idx = j*resX + i;

                    int idxL = ((j + 0)*resX + (i - 1));
                    int idxR = ((j + 0)*resX + (i + 1));
                    int idxB = ((j - 1)*resX + (i + 0));
                    int idxT = ((j + 1)*resX + (i + 0));
                    int idxC = (j*resX + i);

                    float xL = xMat[idxL];
                    float xR = xMat[idxR];
                    float xB = xMat[idxB];
                    float xT = xMat[idxT];
                    float bC = bMat[idxC];

                    float val = (xL + xR + xB + xT + alpha*bC)*invBeta;
                    outMat[idx] = val;
                }
            }
        }
    }

    public void solvePressure( int niters, float[] div, float[] inOutPressure ) {
        float alpha = -this.cellSizeX*this.cellSizeY;
        float beta  = 4.0f;

        for( int i = 0; i < inOutPressure.length; ++i ) {
            inOutPressure[i] = 0.0f;
        }

        this.jacobi( alpha, beta, inOutPressure, div, inOutPressure, niters );
    }

    public void subtractGradient( float[] prs, float[] xvel, float[] yvel ) {
        int iStart = 1;
        int iEnd   = this.resX - 1;
        int jStart = 1;
        int jEnd   = this.resY - 1;

        int resX = this.resX;
        int resY = this.resY;

        for( int j = jStart; j < jEnd; ++j ) {
            for( int i = iStart; i < iEnd; ++i ) {
                int idx = j*resX + i;

                int idxL = ((j + 0)*resX + (i - 1));
                int idxR = ((j + 0)*resX + (i + 1));
                int idxB = ((j - 1)*resX + (i + 0));
                int idxT = ((j + 1)*resX + (i + 0));

                float diffX = prs[idxR] - prs[idxL];
                float diffY = prs[idxT] - prs[idxB];

                float valX = this.halfDivCellSizeX*diffX;
                float valY = this.halfDivCellSizeY*diffY;

                xvel[idx] -= valX;
                yvel[idx] -= valY;
            }
        }
    }

    public void calculateCurl( float[] xvel, float[] yvel, float[] outCurl, float[] outCurlLength ) {
        int iStart = 1;
        int iEnd   = this.resX - 1;
        int jStart = 1;
        int jEnd   = this.resY - 1;

        int resX = this.resX;
        int resY = this.resY;

        for( int j = jStart; j < jEnd; ++j ) {
            for( int i = iStart; i < iEnd; ++i ) {
                int idxL = (j + 0)*resX + (i - 1);
                int idxR = (j + 0)*resX + (i + 1);
                int idxB = (j - 1)*resX + (i + 0);
                int idxT = (j + 1)*resX + (i + 0);

                float dudy = xvel[idxT] - xvel[idxB];
                float dvdx = yvel[idxR] - yvel[idxL];
                float curl = (dudy - dvdx)*0.5f;

                int idx = j*resX + i;
                outCurlLength[idx] = curl;
                outCurl[idx] = Math.abs( curl );
            }
        }
    }

    public void vorticityConfinement( float vorticityScale, float[] xvel, float[] yvel, float[] curl, float[] curlLength, float[] outXVel, float[] outYVel ) {
        int iStart = 1;
        int iEnd   = this.resX - 1;
        int jStart = 1;
        int jEnd   = this.resY - 1;

        int resX = this.resX;
        int resY = this.resY;

        for( int j = jStart; j < jEnd; ++j ) {
            for( int i = iStart; i < iEnd; ++i ) {
                int idxL = (j + 0)*resX + (i - 1);
                int idxR = (j + 0)*resX + (i + 1);
                int idxB = (j - 1)*resX + (i + 0);
                int idxT = (j + 1)*resX + (i + 0);

                float dwdx = (curl[idxR] - curl[idxL])*0.5f;
                float dwdy = (curl[idxT] - curl[idxB])*0.5f;

                float lenSq = dwdx*dwdx + dwdy*dwdy;
                float len = (float)Math.sqrt( lenSq ) + 0.000001f;
                float s = 1.0f/len;
                dwdx *= s;
                dwdy *= s;

                int idx = j*resX + i;
                float v = curlLength[idx];
                outXVel[idx] = xvel[idx] + vorticityScale*dwdy*-v;
                outYVel[idx] = yvel[idx] + vorticityScale*dwdx*v;
            }
        }
    }

    public void update() {
        this.advectAndDiffuse( this.velDiss, this.velVisc, this.dt, this.xvel0, this.yvel0, this.xvel0, this.xvel1 );
        this.advectAndDiffuse( this.velDiss, this.velVisc, this.dt, this.xvel0, this.yvel0, this.yvel0, this.yvel1 );
        this.setBoundary( this.xvel1 );
        this.setBoundary( this.yvel1 );

        this.computeDivergence( this.xvel1, this.yvel1, this.div );
        this.setBoundary( this.div );

        this.solvePressure( 5, this.div, this.prs );
        this.setBoundary( this.prs );

        this.subtractGradient( this.prs, this.xvel1, this.yvel1 );

        this.calculateCurl( this.xvel1, this.yvel1, this.curl, this.curlLength );
        this.setBoundary( this.curl );
        this.setBoundary( this.curlLength );
        //
        float[] tmp = this.xvel0;
        this.xvel0 = this.xvel1;
        this.xvel1 = tmp;
        tmp = this.yvel0;
        this.yvel0 = this.yvel1;
        this.yvel1 = tmp;

        //
        this.vorticityConfinement( this.vorticityScale, this.xvel0, this.yvel0, this.curl, this.curlLength, this.xvel1, this.yvel1 );
        this.setBoundary( this.xvel1 );
        this.setBoundary( this.yvel1 );

        if( this.enableDensity ) {
            this.advectAndDiffuse( this.denDiss, this.denVisc, this.dt, this.xvel0, this.yvel0, this.den0, this.den1 );
            this.setBoundary( this.den1 );
        }

        tmp = this.xvel0;
        this.xvel0 = this.xvel1;
        this.xvel1 = tmp;
        tmp = this.yvel0;
        this.yvel0 = this.yvel1;
        this.yvel1 = tmp;

        if( this.enableDensity ) {
            tmp = this.den0;
            this.den0 = this.den1;
            this.den1 = tmp;
        }
    }
}
