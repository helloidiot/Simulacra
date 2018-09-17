//////////////////////
// GREY SCOTT PLANE //
//////////////////////

// Reference
// http://toxiclibs.org/2010/02/simutils-grayscott/

// PROPERTIES

// Boolean for inititialising the class
boolean grey;

// For choosing the feed type
boolean bGSImage = true;
PImage img;

public class GreyScottPlane extends Artifact{  // Plane is a type of Artifact

  // PROPERTIES
  int wid;
  int hei;
  float scl;

  int feedW = 4;
  int feedH = 4;

  int gsIterations = 10;

  // CONSTRUCTOR
  GreyScottPlane(){
    // Calls the contructor of the super class
    super();
  }

  // SETUP
  // override setup function to create a plane
  public void setup(){
    // Initialise our empty ArrayList and initial Vector
    vertices = new ArrayList<Vert>();
    PVector tempVec = new PVector(0,0,0);

    // Load an image to be used for feed (if not using the creatures)
    // Needs to be the same size as the shape used i.e 128x128 pixels
    if (bGSImage) img = loadImage("simulacra.png");

    int posX = (int)map(mod1, -200, 200, 0, gsWidth);
    int posY = (int)map(mod2, -200, 200, 0, gsHeight);

    // If using the image seed mode, add the image to the toxiclibs grey scott algorithm
    if (bGSImage) gs.seedImage(img.pixels, img.width, img.height);

    for(int i = 0; i < gsWidth; i++){
      for(int j = 0; j < gsHeight; j++){

        tempVec.x = i*gsRes - (gsWidth*scl/2.0);
        tempVec.y = j*gsRes - (gsHeight*scl/2.0);

        float cellValue = gs.v[(i * gsWidth) + j];
        float mod = map(cellValue, 0, 10, 0, greyScottAmt);

        tempVec.z = mod;

        pushVert(tempVec);
      }
    }
  }

  // DRAW
  // override draw function to draw a plane
  public void draw(PGraphics pg, int it){

    // Update the algorithm depending on the number of iterations set by the GUI
    for(int i = 0; i < gsIterations; i++) gs.update(1);

    // If we're in creature mode
    if (bCreatures){

      // Loop through each creature
      for (int i = 0; i < creatures.length; i++){
        // Assign it an empty vector
        creatures[i] = new PVector(0,0,0);

        // Generate an X & Y position value based on a looping simplex noise algorithm
        creatures[i].x = (float)simplexNoise.eval((it + (i*100)) * 0.004, (it + (i*200)) * 0.005);
        creatures[i].y = (float)simplexNoise.eval((it + (i*200)) * 0.004, (it + (i*300)) * 0.005);
        creatures[i].z = 0;

        // Map the returned value to the width and height of the shape
        int x = (int)map(creatures[i].x, -1, 1, 0, gsWidth);
        int y = (int)map(creatures[i].y, -1, 1, 0, gsHeight);

        // And place feed the algorithm at each creatures location
        gs.setRect(x, y, feedW, feedH);

        // This gives the impression of creatures crawling over the shape, pressing down on the raised sections
      }
    }

    // Otherwise, just set a small amount of feed in the centre of the shape
    else if (!bCreatures){
        gs.setRect(gsWidth/2, gsHeight/2, 3, 3);
    }

    pushTransform(pg);

    // draw vertices on plane with triangles
    for(int i = 0; i < gsWidth -1;i++){
      pg.beginShape(TRIANGLE_STRIP);
      for(int j = 0; j < gsHeight - 1 ;j++){

        PVector p = vertices.get( (i * gsWidth) + j ).position;
        PVector p2 = vertices.get( ((i+1) * gsHeight) + j).position;

        pg.vertex(p.x, p.y, p.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();
    }
    popTransform(pg);

  }
} // End of Grey Scott Plane class
