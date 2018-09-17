////////////////////////////////////////////////////////////////////////////////
//                                                                            //
//                           simulacraViewer                                  //
//                                2018                                        //
//                                                                            //
//                        Joseph Rodrigues Marsh                              //
//                                                                            //
//                  MA Computational Art Final Project                        //
//                                                                            //
// A RaspberryPi application for autonomously exhibiting looping videos .     //
// The app is designed to run unattended through the duration of an exhibit.  //
//                                                                            //
//      Requires RaspberryPi running openFrameworks 0.9.8 & ofxOMXPlayer      //
//                      --WILL NOT COMPILE IN OSX--                           //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

#include "ofApp.h"

void ofApp::setup(){

    ofSetLogLevel(OF_LOG_VERBOSE);
    ofSetRectMode(OF_RECTMODE_CENTER);
    
    // Set the background to black so any projector overspill wont be as noticable
    ofSetBackgroundColor(0);
    
    // Assign settings for the video player
    settings.videoPath = videoPath;     // Grab the path for the video to loop
    settings.enableTexture = false;     // Don't use textured mode to save resources
    settings.enableLooping = true;      // Enable looping
    settings.enableAudio = false;       // No need for audio in this case so we can disable it
    
    // Apply these settings to the player
    player.setup(settings);
    
    // Create a centered variable for ease of use
    centreX = ofGetWidth()/2;
    centreY = ofGetHeight()/2;
    
    // Assign the desired width and height of the video
    // In this case a 1:1 ratio of 1080x1080
    videoWidth = 1080;
    videoHeight = videoWidth;
    
    // Grab the total number of frames for debugging
    totalFrames = player.getTotalNumFrames();
    
    // Assign the intital position for the video
    // This is dependant on projector positioning and is hard coded once the exhibit has been installed
    vidX = 420;
    vidY = 0;
    
    // Set the initial presentation mode
    bMode = true; // Draw video at specific coordinates
}

void ofApp::update(){
    // Grab the current frame for debugging
    currentFrame = player.getCurrentFrame();
}

void ofApp::draw(){
    // Draw the video:
    // If in mode 0, draw the video in the centre of the screen
    if (!bMode)     player.draw(centreX, centreY, videoWidth, videoHeight);
    
    // If in mode 1, draw the video at the specified position
    else if (bMode) player.draw(vidX, vidY, videoWidth, videoHeight);
    
    // Draw debugging information
    if (bDebug) debug(60, 60);
    if (bGrid)  drawGrid(20);
    if (bBox)   drawBox(20);
}

// USER INPUT

void ofApp::keyPressed(int key){
    
    switch (key){
        // Switch presentation mode
        case '1':
            bMode = !bMode;
            
        // Player controls
        case 'r':
            player.restartMovie();
        case ' ':
            player.togglePause();
        case 'j':
            player.rewind();
        case 'k':
            player.setNormalSpeed();
        case 'l':
            player.increaseSpeed();
        
        // Shift the video position for fine-tuning after installation
        case OF_KEY_LEFT:
            vidX -= 5;
            break;
        case OF_KEY_RIGHT:
            vidX += 5;
            break;
        case OF_KEY_DOWN:
            vidY += 5;
            break;
        case OF_KEY_UP:
            vidY -= 5;
            break;
            
        // Toggle debugging tools on / off
        case 'd':
            bDebug = !bDebug;
            break;
        case 'g':
            bGrid = !bGrid;
            break;
        case 'b':
            bBox = !bBox;
            break;
    }
}


// DEBUGGING TOOLS

void ofApp::debug(int x, int y){
    
    push();
    
    // Video Info
    ofDrawBitmapStringHighlight(player.getInfo(), x, y, ofColor(255), ofColor::yellow);
    
    // Video position
    ofSetColor(10, 255, 10);
    ofDrawBitmapString("vidX: " + ofToString(vidX), ofGetWidth()/2, ofGetHeight()/2);
    ofDrawBitmapString("vidY: " + ofToString(vidY), ofGetWidth()/2, ofGetHeight()/2 + 20);
    
    // Frame Info
    //ofDrawBitmapStringHighlight("Total Frames: " + ofToString(totalFrames), x, y + 20, ofColor(ofColor::black, 90), ofColor::yellow);
    
    // Frame Info
    //ofDrawBitmapStringHighlight("Current Frame: " + ofToString(currentFrame), x, y + 40, ofColor(ofColor::black, 90), ofColor::yellow);
    
    pop();
}

void ofApp::drawGrid(int spacing){
    
    // Create a transparent grid to aid projector alignment
    
    for (int i = 0; i < ofGetWidth(); i+=spacing){
        for (int j = 0; j < ofGetHeight(); j+=spacing){
            
            // If at the edge of the desired video size, draw a red outline
            if (i == 420 || i == (ofGetWidth()-420) || j == 0 || j == ofGetHeight()){
                push();
                ofSetColor(255,0,0);
                // Horizontal
                ofDrawLine(0, j, ofGetWidth(), j);
                // Vertical
                ofDrawLine(i, 0, i, ofGetHeight());
                pop();
            }
            
            // Otherwise draw a white grid
            else {
                push();
                ofSetColor(255);
                // Horizontal
                ofDrawLine(0, j, ofGetWidth(), j);
                // Vertical
                ofDrawLine(i, 0, i, ofGetHeight());
                pop();
            }
        }
    }
}

void ofApp::drawBox(int spacing){
    
    // Create an opaque square grid to aid projector keystoning.
    push();
    
    int w = 1080;
    int h = 1080;

    ofSetColor(255,0,0);
    ofDrawRectangle(ofGetWidth()/2, ofGetHeight()/2, 1080, 1080);
    
    ofSetColor(255);
    ofTranslate((ofGetWidth()/2)-(w/2), 0);
    
    for (int i = 0; i < w; i+=spacing){
        for (int j = 0; j < h; j+=spacing){
            
            // Horizontal
            ofDrawLine(0, j, w, j);
            // Vertical
            ofDrawLine(i, 0, i, h);
        }
    }
    pop();
}

void ofApp::push(){
    ofPushStyle();
    ofPushMatrix();
}

void ofApp::pop(){
    ofPopStyle();
    ofPopMatrix();
}
