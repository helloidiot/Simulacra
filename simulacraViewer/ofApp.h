#pragma once

#include "ofMain.h"
#include "ofxOMXPlayer.h"

class ofApp : public ofBaseApp{

	public:
        // STANDARD FUNCTIONS
		void setup();
		void update();
		void draw();
    
        // HELPER FUNCTIONS //
        void debug(int x, int y);
        void push();
        void pop();
        void drawGrid(int spacing);
        void drawBox(int spacing);
    
        // USER INTERACTION
		void keyPressed(int key);
    
    // Declare the video player and create a settings object
    ofxOMXPlayer player;
    ofxOMXPlayerSettings settings;
    
    // Functional booleans to switch between different modes
    bool bMode;
    bool bDebug;
    bool bGrid;
    bool bBox;
    
    // Declare the pathof the video to display
    string videoPath = ofToDataPath("movies/simulacra.mov", true);
    
    // Positional values for the video
    int centreX, centreY;
    int vidX, vidY;
    int videoWidth, videoHeight;
    
    // Debug information
    int totalFrames, currentFrame;
};
