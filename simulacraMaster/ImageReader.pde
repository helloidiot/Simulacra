//////////////////
// IMAGE READER //
//////////////////

// This class loads a two-dimensional image, and uses its brightness values to extrude a plane in z-space.

// Boolean for inititialising the class
boolean image;

public class ImageReader extends Plane{

  // PROPERTIES
  PImage img;
  String imageName;

  ImageReader() {
    super();
  }

  // SETUP
  public void setup(){

    // Initialise the empty array
    vertices = new ArrayList<Vert>();

    // Enter the name of the image to load
    imageName = "simulacra";

    // Load the chosen image
    img = loadImage(imageName + ".png");

    // Along with its pixels
    img.loadPixels();

    // Assign the width of the plane to be the same width and height of the loaded image
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

    // Update the pixels
    img.updatePixels();
  }

  public void draw(PGraphics pg, int it){

    pushTransform(pg);

    // draw vertices on plane with triangles
    for(int i = 0; i < wid -1;i++){
      pg.beginShape(TRIANGLE_STRIP);
      for(int j = 0; j < hei - 1 ;j++){

        PVector p = vertices.get( (i * wid) + j ).position;
        PVector p2 = vertices.get( ((i+1) * hei) + j).position;

        pg.vertex(p.x, p.y, p.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();
    }
    popTransform(pg);

  }

  // Find the corresponding vertex and map its brightness value to a chosen range via the GUI
  float getZAt(int i, int j){
    float res = map(brightness(img.pixels[ j * wid + i]), 255, 0, 0, z_Slider);
    return res;
  }

} // End of the Image Reader class
