package org.hai.grfx;

import org.hai.math.mat4;
import org.hai.math.vec3;

public class Camera {

    private float mPixelWidth               = 0.0f;
    private float mPixelHeight              = 0.0f;
    private float mAspectRatio              = this.mPixelWidth / this.mPixelHeight;
    private boolean mPixelAligned           = false;
    private boolean mUpperLeftOrigin        = false;

    private vec3 mEyePoint                  = new vec3( 0.0f, 0.0f,  1.0f );
    private vec3 mTarget                    = new vec3( 0.0f, 0.0f,  0.0f );
    private vec3 mWorldUp                   = new vec3( 0.0f, 1.0f,  0.0f );
    private vec3 mViewDir                   = new vec3( 0.0f, 0.0f, -1.0f );
    private vec3 mNormalizedViewDir         = new vec3( 0.0f, 0.0f, -1.0f );

    // Vertical FOV
    private float mFovDegrees               = 60.0f;
    private float mNearClip                 = 1.0f;
    private float mFarClip                  = 1000.0f;

    private mat4 mModelViewMatrix           = new mat4();
    private mat4 mProjectionMatrix          = new mat4();
    private mat4 mModelViewProjectionMatrix = new mat4();

    /** constructor
     *
     * Creates pixel aligned camera with an upper left origin. Field of view
     * uses default values. Clip values are based on pixel alignment calculations.
     *
     */
    Camera(int pixelWidth, int pixelHeight) {
        this.mPixelWidth        = pixelWidth;
        this.mPixelHeight       = pixelHeight;
        this.mAspectRatio       = this.mPixelWidth / this.mPixelHeight;
        this.mPixelAligned      = true;
        this.mUpperLeftOrigin   = true;

        initialize();
    }

    /** constructor
     *
     * Creates a pixel aligned camera with an upper left origin. Field of view
     * is specified by fov in degrees. Clip values are based on pixel alignment
     * calculations.
     *
     */
    Camera(int pixelWidth, int pixelHeight, float fov) {
        this.mPixelWidth        = pixelWidth;
        this.mPixelHeight       = pixelHeight;
        this.mAspectRatio       = this.mPixelWidth / this.mPixelHeight;
        this.mPixelAligned      = true;
        this.mUpperLeftOrigin   = true;
        this.mFovDegrees        = fov;

        initialize();
    }

    /** constructor
     *
     * Creates pixel aligned camera with an optional upper left origin. Field of view
     * uses default values. Clip values are based on pixel alignment calculations.
     *
     */
    Camera(int pixelWidth, int pixelHeight, boolean upperLeftOrigin) {
        this.mPixelWidth        = pixelWidth;
        this.mPixelHeight       = pixelHeight;
        this.mAspectRatio       = this.mPixelWidth / this.mPixelHeight;
        this.mPixelAligned      = true;
        this.mUpperLeftOrigin   = upperLeftOrigin;


        initialize();
    }

    /** constructor
     *
     * Creates a pixel aligned camera with an optional upper left origin. Field of view
     * is specified by fov in degrees. Clip values are based on pixel alignment
     * calculations.
     *
     */
    Camera(int pixelWidth, int pixelHeight, float fov, boolean upperLeftOrigin) {
        this.mPixelWidth        = pixelWidth;
        this.mPixelHeight       = pixelHeight;
        this.mAspectRatio       = this.mPixelWidth / this.mPixelHeight;
        this.mPixelAligned      = true;
        this.mUpperLeftOrigin   = upperLeftOrigin;
        this.mFovDegrees        = fov;

        initialize();
    }

    /** constructor
     *
     * Creates a camera positioned at (0, 0, 1) looking at (0, 0, 0). Field of view
     * is specified by fov in degrees.
     *
     */
    Camera(int pixelWidth, int pixelHeight, float fov, float near, float far) {
        this.mPixelWidth        = pixelWidth;
        this.mPixelHeight       = pixelHeight;
        this.mAspectRatio       = this.mPixelWidth / this.mPixelHeight;
        this.mPixelAligned      = false;
        this.mUpperLeftOrigin   = false;

        this.mFovDegrees        = fov;
        this.mNearClip          = near;
        this.mFarClip           = far;

        initialize();

        lookAt(new vec3(0, 0, 1), new vec3(0, 0, 0));
    }

    /** constructor
     *
     * Creates a camera positioned at (0, 0, 1) looking at (0, 0, 0) with
     * optional upper left origin. Field of view is specified by fov in degrees.
     *
     */
    Camera(int pixelWidth, int pixelHeight, float fov, float near, float far, boolean upperLeftOrigin) {
        this.mPixelWidth        = pixelWidth;
        this.mPixelHeight       = pixelHeight;
        this.mAspectRatio       = this.mPixelWidth / this.mPixelHeight;
        this.mPixelAligned      = false;
        this.mUpperLeftOrigin   = upperLeftOrigin;

        this.mFovDegrees        = fov;
        this.mNearClip          = near;
        this.mFarClip           = far;

        initialize();

        lookAt(new vec3(0, 0, 1), new vec3(0, 0, 0));
    }

    private void initialize() {
        if (this.mPixelAligned) {
            float eyeX 		= this.mPixelWidth / 2.0f;
            float eyeY 		= this.mPixelHeight / 2.0f;
            float halfFov 	= 3.14159f / 180.0f * (this.mFovDegrees / 2.0f);
            float theTan 	= (float)Math.tan( halfFov );
            float dist 		= eyeY / theTan;

            this.mNearClip 	= dist / 10.0f;
            this.mFarClip 	= dist * 10.0f;

            this.setPerspective( this.mFovDegrees, this.mAspectRatio, this.mNearClip, this.mFarClip );
            this.lookAt( new vec3( eyeX, eyeY, dist ), new vec3( eyeX, eyeY, 0.0f ), vec3.YAXIS );
        }
        else {
            this.setPerspective( this.mFovDegrees, this.mAspectRatio, this.mNearClip, this.mFarClip );
            this.lookAt( this.mEyePoint, this.mTarget, vec3.YAXIS );
        }
    }

    public String toString() {
        String result =
            "pixelWidth  : " + mPixelWidth + "\n" +
            "pixelHeight : " + mPixelHeight;
        return result;
    }

    public vec3 getViewDir() {
        return this.mViewDir;
    }

    public vec3 getNormalizedViewDir() {
        return this.mNormalizedViewDir;
    }

    /** @fn getModelViewMatrix
     *
     * @brief Returns the current model view matrix
     * @returns [mat4] model view matrix
     *
     */
    public mat4 getModelViewMatrix() {
        return this.mModelViewMatrix;
    }

    /** @fn getProjectionMatrix
     *
     * @brief Returns the current projection matrix
     * @returns [mat4] projection matrix
     *
     */
    public mat4 getProjectionMatrix() {
        return this.mProjectionMatrix;
    }

    /** @fn getModelViewProjectionMatrix
     *
     * @brief Returns the current model view projection matrix
     * @returns [mat4] model view projection matrix
     *
     */
    public mat4 getModelViewProjectionMatrix() {
        return this.mModelViewProjectionMatrix;
    }

    /** @fn updateModelViewMatrix
     *
     * @brief Updates the model view matrix using mEyePoint, mTarget, and mWorldUp
     *
     */
    public void updateModelViewMatrix() {
        this.mModelViewMatrix.lookAt( this.mEyePoint, this.mTarget, this.mWorldUp );

        if (this.mPixelAligned && this.mUpperLeftOrigin) {
            this.mModelViewMatrix.scale( 1.0f, -1.0f, 1.0f );
            this.mModelViewMatrix.translate( 0.0f, -this.mPixelHeight, 0.0f );
        }
    }

    /** @fn updateProjectionMatrix
     *
     * @brief Updates the model view matrix using mFovDegrees, mAspectRatio, mNearClip, mFarClip
     *
     */
    public void updateProjectionMatrix() {
        this.mProjectionMatrix.perspective( this.mFovDegrees, this.mAspectRatio, this.mNearClip, this.mFarClip );
    }

    /** @fn updateModelViewProjectionMatrix
     *
     * @brief Updates the model view projection matrix using mProjectionMatrix and mModelViewMatrix
     *
     */
    public void updateModelViewProjectionMatrix() {
        this.mModelViewProjectionMatrix.mul( this.mProjectionMatrix, this.mModelViewMatrix );
    }

    /** @fn lookAt
     *
     * @brief Sets the camera position to eyePoint, looking at target with up vector of worldUp
     * @param eyePoint [vec3] - Coordinate of camera position
     * @param target   [vec3] - Coordinate of look at point for camera
     * @param worldUp  [vec3] - Up vector
     *
     */
    public void lookAt(vec3 eyePoint, vec3 target, vec3 worldUp) {
        this.mEyePoint.copy( eyePoint );
        this.mTarget.copy( target );
        this.mWorldUp.copy( worldUp );
        this.mViewDir.sub( this.mTarget, this.mEyePoint );
        this.mNormalizedViewDir.copyNormalized( this.mViewDir );

        this.updateModelViewMatrix();
        this.updateModelViewProjectionMatrix();
    }

    /** @fn lookAt
     *
     * @brief Sets the camera position to eyePoint, looking at target with up vector of worldUp
     * @param eyePoint [vec3] - Coordinate of camera position
     * @param target   [vec3] - Coordinate of look at point for camera
     *
     */
    public void lookAt(vec3 eyePoint, vec3 target) {
        lookAt(eyePoint, target, vec3.YAXIS);
    }

    /** @fn setPerspective
     *
     * @brief Sets the perspective parameters of the camera
     * @param fovDegrees  [float] - Vertical field of view in degrees
     * @param aspectRatio [float] - Aspect ratio of view port
     * @param nearClip    [float] - Near clipping plane distance
     * @param farClip     [float] - Far clipping plane distance
     *
     */
    public void setPerspective(float fovDegrees, float aspectRatio, float nearClip, float farClip) {
        this.mFovDegrees  = fovDegrees;
        this.mAspectRatio = aspectRatio;
        this.mNearClip    = nearClip;
        this.mFarClip     = farClip;

        this.updateProjectionMatrix();
        this.updateModelViewProjectionMatrix();
    }

    /** @fn setAspectRatio
     *
     * @brief Sets the aspect ratio parameters of the camera
     * @param aspectRatio [float] - Aspect ratio of view port
     *
     */
    public void setAspectRatio(float  aspectRatio ) {
        this.mAspectRatio = aspectRatio;
        this.updateProjectionMatrix();
        this.updateModelViewProjectionMatrix();
    }

    /** create
     *
     * Creates a camera positioned at (0, 0, 1) looking at (0, 0, 0)
     *
     * @param pixelWidth  [int]   - Pixel width
     * @param pixelHeight [int]   - Pixel height
     * @param fov         [float] - Vertical field of view in degrees
     * @param near        [float] - Near clipping plane distance
     * @param far         [float] - Far clipping plane distance
     *
     */
    public static Camera create(int pixelWidth, int pixelHeight, float fov, float near, float far) {
        Camera result = new Camera(pixelWidth, pixelHeight, fov, near, far);
        result.lookAt( new vec3(0, 0, 1), new vec3(0, 0, 0), vec3.YAXIS );
        return result;
    }


    /** createPixelAligned
     *
     * Creates a pixel aligned camera WITHOUT an upper left origin
     *
     * @param pixelWidth  [float] - Pixel width
     * @param pixelHeight [float] - Pixel height
     *
     */
    public static Camera createPixelAligned(int pixelWidth, int pixelHeight) {
        Camera result = new Camera(pixelWidth, pixelHeight);
        result.lookAt( new vec3(0, 0, 1), new vec3(0, 0, 0), vec3.YAXIS );
        return result;
    }

    /** @fn createPixelAligned
     *
     * Creates a pixel aligned camera WITHOUT an upper left origin
     *
     * @param pixelWidth  [float] - Pixel width
     * @param pixelHeight [float] - Pixel height
     * @param fov         [float] - Vertical field of view in degrees
     *
     */
    public static Camera createPixelAligned(int pixelWidth, int pixelHeight, float fov) {
        Camera result = new Camera(pixelWidth, pixelHeight, fov);
        return result;
    }

    /** createPixelAlignedUL
     *
     * Creates a pixel aligned camera WITH an upper left origin
     *
     * @param pixelWidth  [float] - Pixel width
     * @param pixelHeight [float] - Pixel height
     *
     */
    public static Camera createPixelAlignedUL(int pixelWidth, int pixelHeight) {
        final boolean upperLeftOrigin = true;
        Camera result = new Camera(pixelWidth, pixelHeight);
        return result;
    }

    /** createPixelAlignedUL
     *
     * Creates a pixel aligned camera WITH an upper left origin
     *
     * @param pixelWidth  [float] - Pixel width
     * @param pixelHeight [float] - Pixel height
     * @param fov         [float] - Vertical field of view in degrees
     *
     */
    public static Camera createPixelAlignedUL(int pixelWidth, int pixelHeight, float fov) {
        final boolean upperLeftOrigin = true;
        Camera result = new Camera(pixelWidth, pixelHeight, fov);
        return result;
    }
}
