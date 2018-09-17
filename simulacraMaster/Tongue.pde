////////////
// TONGUE //
////////////

// This class is an development of the flower class.
// The intention is to create forms which waggle and wave like a tongue.

float tongueFrequency, tongueMagnitude, tongueIndependence, tongueSpacing, tongueZ, tongueAmount;
int tongueCount, tongueRadius, tongueSamples;
float tongueFreqMin, tongueFreqMax, tongueMagMin, tongueMagMax, tongueIndMin, tongueIndMax;
boolean tongue;

public class Tongue extends Artifact{

  // PROPERTIES
  // float samples;
  PVector tempVec;
  int timer = 0;
  // CONSTRUCTOR
  Tongue(){
    super();
  }

  // SETUP
  public void setup(){
    vertices = new ArrayList<Vert>();

    // If not in animation mode, draw a simple tongue
    if (!animated){
      tempVec = new PVector(0, 0, 0);
      drawTongue( tempVec,  tongueRadius,  tongueFrequency,  tongueMagnitude,  tongueIndependence,  tongueSpacing,  tongueCount, tongueSamples, timer);
    }

  }

  // DRAW
  // override draw function to draw a plane
  public void draw(PGraphics pg, int it){
    if (animated){
      // Create a new empty vector
      tempVec = new PVector(0, 0, 0);

      // Generate a looping noise value for shape animation
      float t = 1.0 * it / numFrames_Slider;
      float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

      // MOdulate the frequency, magnitude and independence of the shape with GUI specified values
      tongueFrequency = map2(ns, -1, 1, tongueFreqMin, tongueFreqMax, QUADRATIC, EASE_IN_OUT);
      tongueMagnitude = map2(ns, -1, 1, tongueMagMin, tongueMagMax, QUADRATIC, EASE_IN_OUT);
      tongueIndependence = map2(ns, -1, 1, tongueIndMin, tongueIndMax, QUADRATIC, EASE_IN_OUT);

      // Draw the tongue
      drawTongue( tempVec,  tongueRadius,  tongueFrequency,  tongueMagnitude,  tongueIndependence,  tongueSpacing,  tongueCount, tongueSamples, it);
    }

    pushTransform(pg);
    pg.beginShape(TRIANGLE_STRIP);
      for (int j = 0; j < vertices.size()-tongueSamples; j++){

        // Grab the first vertex in the array
        PVector p1 = vertices.get(j).position;

        // And the corresponding vertex on the next circle
        PVector p2 = vertices.get((j + tongueSamples)).position;

        // Add them to the buffer
        pg.vertex(p1.x, p1.y, p1.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();


    popTransform(pg);
  }

  // Draws a deformed circle depending on GUI specified values
  void drawCircle(PVector v, float radius, float frequency, float magnitude, float seed, float s ){
    for (int i = 0; i < s; i++){
        float angle = (2 * PI * i) / s;

        float x = radius * sin(angle);
        float y = radius * cos(angle);

        float deformation = (float)simplexNoise.eval(x * frequency, y * frequency, seed) + 1;
        float z = radius * (1 + magnitude * deformation);
        float rad = radius * (1 + magnitude * deformation);

        pushVert(v.x + rad * x, v.y + rad * y, v.z + rad * z);
    }
  }


  void drawTongue(PVector v, float radius, float frequency, float magnitude, float independence, float spacing, float count, float s, int time){

    // Adjust the radius so it's roughly the same size irregardless of magnitude
    radius /= (magnitude + 1);

    // Create a stack of circles
    for (int i = 0; i < count; i++) {

        // Draw the initial circle
        drawCircle(v, radius, frequency, magnitude, i * independence, s);

        // Create a scaled time value
        float t = 1.0 * time / numFrames_Slider;

        // Generate a looping noise value depending on the position of the circle in the stack
        float noiseX = (float)simplexNoise.eval(scale_Slider * (i*mod1), scale_Slider * (i*mod1), radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
        float noiseY = (float)simplexNoise.eval(scale_Slider * (i*mod2), scale_Slider * (i*mod2), radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

        // Map the returned noise value to the GUI specified range
        v.x += map2(noiseX, -1, 1, -tongueAmount, tongueAmount, QUADRATIC, EASE_IN_OUT);
        v.y += map2(noiseY, -1, 1, -tongueAmount, tongueAmount, QUADRATIC, EASE_IN_OUT);

        // shrink the radius of the next circle
        radius *= (1 - spacing);

        // If the radius reaches the minimum, close the end of the shape
        if (radius <= 3) radius = 0;

        // Assign the value to the Z vertex
        v.z += tongueZ;

    }
  }
} // End of Tongue class
