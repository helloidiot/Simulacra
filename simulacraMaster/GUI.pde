///////////////
// GUI CLASS //
///////////////

// GUI vars
String saveLocation;
String notification = " ";
int stroke_Weight, stroke_Colour;
Textlabel fpsLbl, noiseLbl, toggleNoiseLbl, extrudeLbl, toggleExtLbl, styleLbl, transformLbl, shapeLbl, mouseXLbl, mouseYLbl, verticesLbl, notificationLbl;
boolean toggle_axis = false, toggle_grid = false, auto_update = true;

// GUI positioning variables
int buffer = 96;
int initPosY = 300;
int initPosX = 10;
int sliderBuffer = 20;
int sliderWidth = 96;
int sliderHeight = 16;
int buttonWidth = 64;
int buttonHeight = 16;
int toggleRes = 16;

// GUI Colors
color pink = color(216,113,136);
color lightGrey = color(170,170,170);
color darkGrey = color(44,48,55);

// Transform vars
float rotationX, rotationY, rotationZ;
float scaleMod;

// Noise vars
float mod1, mod2;
float x_Slider, y_Slider, z_Slider;
float xMin, xMax, yMin, yMax, zMin, zMax;
float radius_Slider, scale_Slider;
int numFrames_Slider, numFramesMult;

// Browian vars
int numIterations_Slider;
float amp_Slider, freq_Slider, persistence_Slider;
float scaleMin, scaleMax, radiusMin, radiusMax;

// Grey scott vars
float greyScottAmt;

// Toggles
boolean toggle_X = true, toggle_Y = true, toggle_Z = true;
boolean animated = false;
boolean animN, sineN, sine;
boolean toggle_Lights = true, toggle_Wireframe = true;
boolean brownian = false;

class GUI{

  void init(PApplet p){

    // Initialise controlP5
    cp5 = new ControlP5(p);
    cp5.setAutoDraw(false);

    // Change the default font
    PFont f = createFont("heart font by syoko",12);
    ControlFont font = new ControlFont(f);

    // Set GUI styling
    cp5.setColorForeground(lightGrey);
    cp5.setColorBackground(darkGrey);
    cp5.setFont(font);
    cp5.setColorActive(pink);

    // Draw labels for fps and values
    int infoPosY = 10;
    int infoBuffer = 15;
    fpsLbl = new Textlabel(cp5,"FPS",initPosX,infoPosY,128,16);
    mouseXLbl = new Textlabel(cp5,"MOUSEX",initPosX,(infoPosY+=infoBuffer),128,16);
    mouseYLbl = new Textlabel(cp5,"MOUSEY",initPosX,(infoPosY+=infoBuffer),128,16);
    verticesLbl = new Textlabel(cp5,"VERTICES",initPosX,(infoPosY+=infoBuffer),128,16);

    // Notifications
    push();
    notificationLbl = cp5.addTextlabel("noticationLbl").setPosition(initPosX,(infoPosY+=infoBuffer)).setSize(128,16).setColorValue(orange);
    pop();

    // Helper toggles
    int helperPosY = 100;
    cp5.addToggle("auto_update").setLabel("U/D").setPosition(initPosX,helperPosY).setSize(toggleRes,toggleRes);
    cp5.addToggle("toggle_grid").setLabel("GRID").setPosition(initPosX+30,helperPosY).setSize(toggleRes,toggleRes);
    cp5.addToggle("toggle_axis").setLabel("AXIS").setPosition(initPosX+60,helperPosY).setSize(toggleRes,toggleRes);
    cp5.addToggle("toggle_Lights").setLabel("LIGHT").setPosition(initPosX+90,helperPosY).setSize(toggleRes,toggleRes);
    cp5.addToggle("toggle_Wireframe").setLabel("WIRE").setPosition(initPosX+120,helperPosY).setSize(toggleRes,toggleRes);

    // Styling sliders
    int stylePosY = 140;
    styleLbl = new Textlabel(cp5,"STYLE",initPosX, stylePosY, 130, 16);
    cp5.addSlider("stroke_Weight").setLabel("weight").setPosition(initPosX,(stylePosY+=sliderHeight)).setSize(sliderWidth,8).setValue(1).setRange(0, 5);
    cp5.addSlider("stroke_Colour").setLabel("colour").setPosition(initPosX,(stylePosY+=sliderHeight)).setSize(sliderWidth,8).setValue(1).setRange(0, 255);

    // Function buttons
    cp5.addButton("recreate").setPosition(initPosX,650).setSize(buttonWidth,buttonHeight).setLabel("RECREATE");
    cp5.addButton("exportMesh").setPosition(initPosX,670).setSize(buttonWidth,buttonHeight).setLabel("EXPORTMESH");
    cp5.addButton("exportMeshSequence").setPosition(initPosX,690).setSize(buttonWidth,buttonHeight).setLabel("SEQUENCE");
    cp5.addButton("saveSettings").setPosition(1080,690).setSize(buttonWidth-20,buttonHeight).setLabel("SAVE");
    cp5.addButton("loadSettings").setPosition(1080+(50),690).setSize(buttonWidth-20,buttonHeight).setLabel("LOAD");

    // Transformation sliders
    int transformPosY = 185;
    transformLbl = new Textlabel(cp5,"TRANSFORM",7, transformPosY, 130, 16);
    cp5.addSlider("scaleMod").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(sliderWidth,8).setRange(0.0,3.0).setValue(1.0).setLabel("scale");
    cp5.addSlider("rotationX").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(sliderWidth,8).setRange(0.0,0.2).setValue(0.0).setLabel("rotX");
    cp5.addSlider("rotationY").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(sliderWidth,8).setRange(0.0,0.2).setValue(0.0).setLabel("rotY");
    cp5.addSlider("rotationZ").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(sliderWidth,8).setRange(0.0,0.2).setValue(0.0).setLabel("rotZ");
    cp5.addButton("resetRot").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(buttonWidth,buttonHeight).setLabel("RESET ROT");
    cp5.addButton("resetScale").setPosition(initPosX+buttonWidth+5,transformPosY).setSize(buttonWidth,buttonHeight).setLabel("RESET SCL");

    /////////////////////////////////////////
    // Conditional controls for each shape //
    /////////////////////////////////////////

    int shapeInitPosY = 300;
    shapeLbl = new Textlabel(cp5,"SHAPE VALUES",7,shapeInitPosY,130,16);

    // Plane
    if (plane){
      cp5.addSlider("planeWidth").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(128).setRange(0,256).setLabel("width");
      cp5.addSlider("planeHeight").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(128).setRange(0,256).setLabel("height");
      cp5.addSlider("planeRes").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(2).setRange(0.0,10.0).setLabel("resolution");
    }

    // Flower
    else if (flower){
      cp5.addSlider("flowerRadius").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(10).setRange(0, 30).setLabel("radius");
      cp5.addSlider("flowerFrequency").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1).setRange(0,1.0).setLabel("frequency");
      cp5.addSlider("flowerMagnitude").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1).setRange(0.0,1.0).setLabel("magnitude");
      cp5.addSlider("flowerIndependence").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1).setRange(0.0,1.0).setLabel("independence");
      cp5.addSlider("flowerSpacing").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.01).setRange(0.00,0.50).setLabel("spacing");
      cp5.addSlider("flowerCount").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(100).setRange(0,1000).setLabel("count");
      cp5.addSlider("flowerZ").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(-0.50).setRange(20,-20).setLabel("posZ");
      cp5.addSlider("flowerSamples").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(50.0).setRange(0,200).setLabel("samples");
      cp5.addSlider("flowerFreqMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("freqMin");
      cp5.addSlider("flowerFreqMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1).setRange(0.0,1.0).setLabel("freqMax");
      cp5.addSlider("flowerMagMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("magMin");
      cp5.addSlider("flowerMagMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("magMax");
      cp5.addSlider("flowerIndMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("indMin");
      cp5.addSlider("flowerIndMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1).setRange(0.0,1.0).setLabel("indMax");
    }

    // Sphere
    else if (sphere){
      cp5.addSlider("sphereRadius").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(64).setRange(0, 100).setLabel("radius");
      cp5.addSlider("sphereResW").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(64).setRange(0,100).setLabel("resW");
      cp5.addSlider("sphereResH").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(64).setRange(0.0,100.0).setLabel("resH");
      cp5.addSlider("sphereAmount").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,100.0).setLabel("resH");
    }

    // Cube
    else if (cube){
      cp5.addSlider("cubeWidth").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(32).setRange(0, 100).setLabel("width");
      cp5.addSlider("cubeHeight").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(32).setRange(0,100).setLabel("height");
      cp5.addSlider("cubeDepth").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(64).setRange(0.0,100).setLabel("depth");
      cp5.addSlider("cubeSpacing").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(5).setRange(0.0,100).setLabel("spacing");
    }

    // Tongue
    else if (tongue){
      cp5.addSlider("tongueRadius").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(9.0).setRange(0, 30).setLabel("radius");
      cp5.addSlider("tongueFrequency").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0,1).setLabel("frequency");
      cp5.addSlider("tongueMagnitude").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("magnitude");
      cp5.addSlider("tongueIndependence").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("independence");
      cp5.addSlider("tongueSpacing").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1).setRange(0.0,0.1).setLabel("spacing");
      cp5.addSlider("tongueCount").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(11).setRange(0,100).setLabel("count");
      cp5.addSlider("tongueZ").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(-10.0).setRange(0.0,-100).setLabel("Z");
      cp5.addSlider("tongueSamples").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(39.0).setRange(0,200).setLabel("samples");
      cp5.addSlider("tongueAmount").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(5.0).setRange(0,30).setLabel("amount");
      cp5.addSlider("tongueFreqMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("freqMin");
      cp5.addSlider("tongueFreqMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1).setRange(0.0,1.0).setLabel("freqMax");
      cp5.addSlider("tongueMagMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("magMin");
      cp5.addSlider("tongueMagMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("magMax");
      cp5.addSlider("tongueIndMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,1.0).setLabel("indMin");
      cp5.addSlider("tongueIndMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1).setRange(0.0,1.0).setLabel("indMax");
    }

    // Hyperbolic paraboloid
    else if (hyperbolic){
      cp5.addSlider("hyperbolicWidth").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(128).setRange(0,256).setLabel("width");
      cp5.addSlider("hyperbolicHeight").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(128).setRange(0,256).setLabel("height");
      cp5.addSlider("hyperbolicRes").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(2).setRange(0.0,10.0).setLabel("resolution");
      cp5.addSlider("hyperbolicWave").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.5).setRange(0.0,2.0).setLabel("wave");
      cp5.addSlider("waveMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,2.0).setLabel("waveMin");
      cp5.addSlider("waveMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(0.0,2.0).setLabel("waveMax");
      cp5.addSlider("hyperbolicFx").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(3.0).setRange(-3.0,3.0).setLabel("fx");
      cp5.addSlider("fxMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.3).setRange(0.0,3.0).setLabel("fxMin");
      cp5.addSlider("fxMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.3).setRange(0.0,3.0).setLabel("fxMax");
      cp5.addSlider("hyperbolicFy").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(3.0).setRange(-3.0,3.0).setLabel("fy");
      cp5.addSlider("fyMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.3).setRange(0.0,3.0).setLabel("fyMin");
      cp5.addSlider("fyMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.3).setRange(0.0,3.0).setLabel("fyMax");
      cp5.addSlider("hyperbolicAmplify").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(1.0).setRange(-10.0,10.0).setLabel("amplify");
      cp5.addSlider("ampMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-10.0,10.0).setLabel("ampMin");
      cp5.addSlider("ampMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-10.0,10.0).setLabel("ampMax");
    }

    // Extrusion values
    int extInitPosX = 1080;
    int extInitPosY = 20;
    extrudeLbl = new Textlabel(cp5,"EXTRUSION",extInitPosX,extInitPosY,130,16);
    cp5.addSlider("x_Slider").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("X");
    cp5.addSlider("xMin").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("xMin");
    cp5.addSlider("xMax").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("xMax");
    cp5.addSlider("y_Slider").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("Y");
    cp5.addSlider("yMin").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("yMin");
    cp5.addSlider("yMax").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("yMax");
    cp5.addSlider("z_Slider").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("Z");
    cp5.addSlider("zMin").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("zMin");
    cp5.addSlider("zMax").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0).setRange(-100,100).setLabel("zMax");

    // Extrusion toggles
    toggleExtLbl = new Textlabel(cp5,"TOGGLE EXTRUSION",extInitPosX,(extInitPosY+=sliderBuffer),130,16);
    cp5.addToggle("toggle_X").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(toggleRes,toggleRes).setLabel("X");
    cp5.addToggle("toggle_Y").setPosition(extInitPosX+(toggleRes*2),extInitPosY).setSize(toggleRes,toggleRes).setLabel("Y");
    cp5.addToggle("toggle_Z").setPosition(extInitPosX+(toggleRes*4),extInitPosY).setSize(toggleRes,toggleRes).setLabel("Z");
    cp5.addToggle("resetExtrude").setPosition(extInitPosX+(toggleRes*6),extInitPosY).setSize(toggleRes,toggleRes).setLabel("RESET");

    // Noise values
    int noiseInitPosX = 1080;
    int noiseInitPosY = 280;
    noiseLbl = new Textlabel(cp5,"NOISE",noiseInitPosX,noiseInitPosY,130,16);
    cp5.addSlider("mod1").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setValue(0.1).setRange(-200,200).setLabel("mod1");
    cp5.addSlider("mod2").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(-200,200).setValue(4).setLabel("mod2");
    cp5.addSlider("radius_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setValue(0.5).setRange(0,2).setLabel("radius");
    cp5.addSlider("scale_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setValue(0.1).setRange(0.0,0.30).setLabel("scale");
    cp5.addSlider("numFrames_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0,500).setValue(161).setLabel("numFrames");
    cp5.addSlider("numFramesMult").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(1,4).setValue(1).setLabel("frameMult");
    cp5.addSlider("scaleMin").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0.0,0.30).setValue(0.0).setLabel("scaleMin");
    cp5.addSlider("scaleMax").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0.0,0.30).setValue(0.0).setLabel("scaleMax");
    cp5.addSlider("radiusMin").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0.0,2.0).setValue(0.0).setLabel("radiusMin");
    cp5.addSlider("radiusMax").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0.0,2.0).setValue(0.0).setLabel("radiusMax");
    cp5.addSlider("greyScottAmt").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(-300.0,300.0).setValue(0.0).setLabel("greyAmt");

    // Brownian noise values
    cp5.addSlider("numIterations_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setRange(1,12).setValue(4).setLabel("iterations");
    cp5.addSlider("amp_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setRange(1.0,5.0).setValue(1.0).setLabel("amp");
    cp5.addSlider("freq_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setRange(0.0,0.1).setValue(0.00).setLabel("frequency");
    cp5.addSlider("persistence_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setRange(0.0,2.0).setValue(.5).setLabel("persistence");

    // Noise toggles
    toggleNoiseLbl = new Textlabel(cp5,"TOGGLE ANIMATION TYPE",noiseInitPosX,(noiseInitPosY+=sliderBuffer),130,16);
    cp5.addButton("animated").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,toggleRes).setLabel("animated");
    cp5.addToggle("animN").setPosition(noiseInitPosX,(noiseInitPosY+=(sliderBuffer))).setSize(toggleRes,toggleRes).setLabel("NS");
    cp5.addToggle("sineN").setPosition(noiseInitPosX+(toggleRes*2),noiseInitPosY).setSize(toggleRes,toggleRes).setLabel("SN");
    cp5.addToggle("sine").setPosition(noiseInitPosX+(toggleRes*4),noiseInitPosY).setSize(toggleRes,toggleRes).setLabel("SIN");
    cp5.addToggle("bReaction").setPosition(noiseInitPosX+(toggleRes*6),noiseInitPosY).setSize(toggleRes,toggleRes).setLabel("GREY SCOTT");

    // END init()
  }

  void display(PApplet p){

    // Disable camera control on GUI interaction
    if (cp5.isMouseOver()) {
      cam.setActive(false);
    } else {
      cam.setActive(true);
    }

    // Draw the GUI on top of camera view
    hint(DISABLE_DEPTH_TEST);
    cam.beginHUD();
    cp5.draw();
    drawLabels(p);
    cam.endHUD();
    hint(ENABLE_DEPTH_TEST);
  }

  void drawLabels(PApplet p){

    // Information labels
    fpsLbl.setValueLabel("FPS:      " + floor(frameRate));
    mouseXLbl.setValueLabel("MOUSEX:   " + mouseX);
    mouseYLbl.setValueLabel("MOUSEY:   " + mouseY);
    verticesLbl.setValueLabel("VERTICES: " + shape1.vertices.size());
    fpsLbl.draw(p);
    mouseXLbl.draw(p);
    mouseYLbl.draw(p);
    verticesLbl.draw(p);
    styleLbl.draw(p);
    transformLbl.draw(p);

    // Notifications
    notificationLbl.setValueLabel(notification);
    notificationLbl.draw(p);

    // Title labels
    extrudeLbl.draw(p);
    toggleExtLbl.draw(p);
    toggleNoiseLbl.draw(p);
    noiseLbl.draw(p);
    shapeLbl.draw(p);
  }
}
