////////////////////
// IMAGE SEQUENCE //
////////////////////

// This class is similar to Image Sequence, but it loads a sequence of images, allowing for the import and modulation of videos in 3D space.
// Videos should be exported as Photo JPEG sequences and name sequentially according to their frame i.e. '[sequenceName]0.png', '[sequenceName]1.png' etc.
// Remember to also assign the amount of frames to 'frameAmount'.

// Reference
// https://processing.org/examples/sequential.html

// Boolean for inititialising the class
boolean imgSeq;

public class ImageSequence extends Plane{

  // PROPERTIES
  PImage img;
  String sequenceName;
  int frameCounter = 0;
  int frameAmount = 235;

  ImageSequence() {
    super();
  }

  // SETUP
  public void setup(){
    // Initialise the empty array
    vertices = new ArrayList<Vert>();

    // Create an empty vector
    // PVector tempVec = new PVector(0,0,0);

    // Enter the name of the image to load
    sequenceName = "eyes";

    // Load the image by appending the current frame number
    img = loadImage(sequenceName + "/" + frameCounter + ".png");
    img.loadPixels();

    // Assign the width and height of the plane to be the same as the image
    wid = img.width;
    hei = img.height;
    scl = 2;

    // Loop through every vertex, assigning an X & Y value, while calling a custom function to assign a Z value
    for(int i = 0; i < wid; i++){
      for(int j = 0; j < hei; j++){

        pushVert(i*scl - (wid*scl/2.0),
                 j*scl - (hei*scl/2.0),
                 getZAt(i, j));
      }
    }

    img.updatePixels();

    // Advance to the next frame
    frameCounter++;

    // If the end of the sequence is reached, return to the first image in the sequence
    if (frameCounter >= frameAmount){
      frameCounter = 0;
    }
  }

  public void draw(PGraphics pg, int it){

    pushTransform(pg);

    // draw vertices on plane with triangles
    for(int i = 0; i < wid - 1; i++){
      pg.beginShape(TRIANGLE_STRIP);
      for(int j = 0; j < hei - 1 ; j++){

        PVector p = vertices.get((i * wid) + j).position;
        PVector p2 = vertices.get(((i+1) * hei) + j).position;

        pg.vertex(p.x, p.y, p.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();
    }
    popTransform(pg);

    frameCounter++;
  }

  // Find the corresponding vertex and map its brightness value to a chosen range via the GUI
  float getZAt(int i, int j){
    float res = map(brightness(img.pixels[ j * wid + i]), 255, 0, 0, z_Slider);
    return res;
  }

} // End of the Image Sequence class.
