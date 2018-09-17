////////////////
// VERT CLASS //
////////////////

// This class is the core of the animations. Every vectors current position is modulated by creating
// a new value and adding that its original position. Every Vector stored in the array is a type of Vert,
// essentially two vectors added together.
//
// The most important variables for noise modulation are the 'scale' and 'radius' sliders. Scale changes
// the size of the area to draw noise from, radius increases and decreases the the speed of modulation by
// drawing smaller/larger circles in noise space.
//
// the Brownian noise modulator has it's own unique controls for amplitude, frequency and persistence.

class Vert{

  // PROPERTIES
  PVector initPosition;
  PVector position;

  // CONSTRUCTOR
  Vert(float x, float y, float z){

    // Set initial position according to the shape class setup()
    initPosition  = new PVector(x, y, z);

    // Create a copy of that position to modulate
    position = initPosition.copy();
  }

  // For every frame, add the modulated position to the initial position
  // These are to be uncommented for use. Some work well with some shapes, and not with others
  void update(int i){

    position = PVector.add(initPosition, getSimplexMod(i) );
    // position = PVector.add(initPosition, ripples(i) );
    // position = PVector.add(initPosition, floorMod(i) );
    // position = PVector.add(initPosition, headMod(i) );
    // position = PVector.add(initPosition, bulgeMod(i) );
    // position = PVector.add(initPosition, sourMod(i) );
    // position = PVector.add(initPosition, depressing(i) );
    // position = PVector.add(initPosition, turbulenceMod(i) );
    // position = PVector.add(initPosition, brownianSimplex(i) );
    // position = PVector.add(initPosition, brownianRidged(i) );
    // position = PVector.add(initPosition, sheetsMod(i) );
    // position = PVector.add(initPosition, squidger(i) );
    // position = PVector.add(initPosition, sinkMod(i) );
  }

  // MODULATORS //

  // A basic noise modulator using the built in Perlin noise in Processing.
  PVector getNoise(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    // Grab the desired number of frames from the GUI
    int numFrames = numFrames_Slider;

    // And create a scaled time value
    float t = 1.0 * i / numFrames;

    float radius = radius_Slider;
    float scale = scale_Slider;

    float nsX = noise(scale * position.x);
    float nsY = noise(scale * position.y);
    float nsZ = noise(scale * position.z);
    float ns = noise(radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

    float zPos = map(nsX, -1, 1, -z_Slider, z_Slider);
    float xPos = map(nsY, -1, 1, -x_Slider, x_Slider);
    float yPos = map(nsZ, -1, 1, -y_Slider, y_Slider);

    if (toggle_Y) res.y = yPos;
    if (toggle_X) res.x = xPos;
    if (toggle_Z) res.z = zPos;

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
  }


  // Create a looping noise formula using 4D Simplex noise
  // This algorithm draws a circle in noise space to enable the values to return to the beginning and loop.
  // This is the core basis for animation used in Simulacra

  // Looping Simplex Noise by Etienne Jacob - https://necessarydisorder.wordpress.com/2017/11/15/drawing-from-noise-and-then-making-animated-loopy-gifs-from-there/
  // An example GIF by Golan Levin can be found here;
  // https://giphy.com/gifs/loop-processing-noise-xUOxeU2ELSPeTbevle

  PVector getSimplexMod(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    float t = 1.0 * i / numFrames_Slider;

    float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
    // float nsX = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.z, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
    // float nsY = (float)simplexNoise.eval(scale_Slider * position.y, scale_Slider * position.z, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
    // float nsZ = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

    if (animated){
      if (toggle_X) res.x = map2(ns, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
      if (toggle_Y) res.y = map2(ns, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
      if (toggle_Z) res.z = map2(ns, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    }
    else if (!animated){
      if (toggle_X) res.x = map(ns, -1, 1, -x_Slider, x_Slider);
      if (toggle_Y) res.y = map(ns, -1, 1, -y_Slider, y_Slider);
      if (toggle_Z) res.z = map(ns, -1, 1, -z_Slider, z_Slider);
    }

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;

  }

  // Ripple Mod
  // This modulator uses a combination of Simplex noise and the distance function to obscure the original noise pattern
  // Tends to work best on flat shapes such as Planes and Hyperbolic Paraboloids
  PVector ripples(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    int numFrames = numFrames_Slider;
    float radius = radius_Slider;

    float scale = scale_Slider;

    float t = 1.0 * i / numFrames;

    float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

    float rippler = map(ns, -1, 1, 0, mod2);
    float xPos = map(ns, -1, 1, 0, mod1);
    float yPos = map(ns, -1, 1, 0, mod1);
    float distance = dist(res.x, res.y, position.x / xPos, position.y / yPos);
    float result = (rippler + distance);

    if (animated){
      res.x = map2(sin(result), -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
      res.y = map2(sin(result), -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
      res.z = map2(sin(result), -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    }
    else if (!animated){
      res.x = map2(sin(result), -1, 1, 0, x_Slider, QUADRATIC, EASE_IN_OUT);
      res.y = map2(sin(result), -1, 1, 0, y_Slider, QUADRATIC, EASE_IN_OUT);
      res.z = map2(sin(result), -1, 1, 0, z_Slider, QUADRATIC, EASE_IN_OUT);
    }

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
}

// Turbulent Noise Mod
// Reference - https://lodev.org/cgtutor/randomnoise.html

  PVector turbulenceMod(int i){

    // Create an empty vector
    PVector res = new PVector(0,0,0);

    // Create a scaled time value
    float t = 1.0 * i / numFrames_Slider;


    float n = map(t, -1, 1, -mod2, mod2);
    float value = 0;
    float size = 12;
    float initialSize = size;
    float radius = radius_Slider;
    float scale = scale_Slider;

    // Calculate turbulence
    while (size > 1){
      value += (float)simplexNoise.eval(position.x * scale, position.y * scale, position.z / (mod1/10) + ((mod2/10) * n),radius * cos(TWO_PI * n)) * size;
      size /= 4.0;
    }

    float result = (mod2/10) * value / initialSize;

    if (toggle_X) res.x += map(result, -1, 1, -x_Slider, x_Slider);
    if (toggle_Y) res.y += map(result, -1, 1, -y_Slider, y_Slider);
    if (toggle_Z) res.z += map(result, -1, 1, -z_Slider, z_Slider);

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
}

  // Squidge mod - A modulator for creating squishy interactions
  PVector squidger(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    int numFrames = numFrames_Slider;
    float radius = radius_Slider;
    float scale = scale_Slider;

    float t = 1.0 * i / numFrames;

    float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

    float rippler = map(ns, -1, 1, 0, mod2);
    float xPos = map(ns, -1, 1, -(mod1/5), (mod1/5));
    float yPos = map(ns, -1, 1, -(mod2/5), (mod2/5));
    float distance = dist(res.x, res.y, position.x / xPos, position.y / yPos);
    float result = 100 / (rippler + distance);

    if (toggle_X) res.x += map(result, -1, 1, -(x_Slider/4), (x_Slider/4));
    if (toggle_Y) res.y += map(result, -1, 1, -(y_Slider/4), (y_Slider/4));
    if (toggle_Z) res.z += map(result, -1, 1, -(z_Slider/4), (z_Slider/4));

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
  }

  PVector sinkMod(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    int numFrames = numFrames_Slider;
    float radius = radius_Slider;
    float scale = scale_Slider;

    float t = 1.0 * i / numFrames;

    float poker = map(sin(t * 2.0), -1, 1, mod1, mod1);
    float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));
    float spiroDist = dist(res.x, res.y, position.x + map(sin(i * 0.2), -1, 1, mod2, -mod2), position.y + map(cos(i * 0.2), -1, 1, -mod2, mod2));
    float result = 100 / (poker + spiroDist);

    if (toggle_X) res.x = map(result, 0, 25, -x_Slider, x_Slider);
    if (toggle_Y) res.y = map(result, 0, 25, -y_Slider, y_Slider);
    if (toggle_Z) res.z = map(result, 0, 25, -z_Slider, z_Slider);

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
}

// Brownian Simplex Modulator
// Reference - https://cmaher.github.io/posts/working-with-simplex-noise/

PVector brownianSimplex(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  int num_iterations = numIterations_Slider;
  int numFrames = numFrames_Slider;
  float amp = amp_Slider;
  float maxAmp = 0;
  float freq = freq_Slider;
  float persistence = persistence_Slider;
  float radius = radius_Slider;
  float t = 1.0 * i / numFrames;

  //add successively smaller, higher-frequency terms
     for(i = 0; i < num_iterations; i++){
       float ns = (float)simplexNoise.eval(position.x * freq, position.y * freq, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t)) * amp;

       if (animated){
         res.z += map2(ns, -1, 1, zMin/10, zMax/10, QUADRATIC, EASE_IN_OUT);
         res.x += map2(ns, -1, 1, xMin/10, xMax/10, QUADRATIC, EASE_IN_OUT);
         res.y += map2(ns, -1, 1, yMin/10, yMax/10, QUADRATIC, EASE_IN_OUT);
       }
       else {
         res.z += map(ns, -1, 1, 0, z_Slider);
         res.x += map(ns, -1, 1, 0, x_Slider);
         res.y += map(ns, -1, 1, 0, y_Slider);
       }

       maxAmp += amp;
       amp *= persistence;
       freq *= 2;
    }
     //take the average value of the iterations
     res.z /= maxAmp;

     //normalize the result
     if (toggle_X) res.x = res.x * (255 - 0) / 2 + (255 + 0) / 2;
     if (toggle_Y) res.y = res.y * (255 - 0) / 2 + (255 + 0) / 2;
     if (toggle_Z) res.z = res.z * (255 - 0) / 2 + (255 + 0) / 2;

     // Advance the iterator
     i++;

     // Return the modulated value
     return res;

}

// Brownian Ridged Modulator
// An extensive of the Brownian noise mod.
// Reference - https://cmaher.github.io/posts/working-with-simplex-noise/
PVector brownianRidged(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  int num_iterations = numIterations_Slider;
  int numFrames = numFrames_Slider;
  float amp = amp_Slider;
  float maxAmp = 0;
  float freq = freq_Slider;
  float persistence = persistence_Slider;
  float radius = radius_Slider;
  float t = 1.0 * i / numFrames;

  //add successively smaller, higher-frequency terms, with a different noise value for each axis
     for(i = 0; i < num_iterations; i++){
         res.z += simplexNoise.eval(position.x * freq, position.y * freq, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t)) * amp;
         res.x += simplexNoise.eval(position.x * freq, position.z * freq, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t)) * amp;
         res.y += simplexNoise.eval(position.y * freq, position.z * freq, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t)) * amp;
         maxAmp += amp;
         amp *= persistence;
         freq *= 2;
       }

     // Average the iterations
     res.z /= maxAmp;

     // Normalise the result
     if (toggle_X) res.x = abs(map(res.x, -1, 1, 0, x_Slider));
     if (toggle_Y) res.y = abs(map(res.y, -1, 1, 0, y_Slider));
     if (toggle_Z) res.z = abs(map(res.z, -1, 1, 0, z_Slider));

     // Advance the iterator
     i++;

     // Return the modulated Vector
     return res;

}

PVector sheetsMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  int numFrames = numFrames_Slider;
  float radius = radius_Slider;

  float scale = scale_Slider;

  float t = 1.0 * i / numFrames;

  float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

  float rippler = map(ns, -1, 1, -mod2, mod2);
  float xPos = map(ns, -1, 1, 0, x_Slider);
  float yPos = map(ns, -1, 1, 0, y_Slider);
  float distance = dist(res.x, res.y, position.x / xPos, position.y / yPos);
  float result = (mod1 * 10) / (rippler + distance);

  if (animated){
    if (toggle_X) res.x = map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y = map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Z) res.z = map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
  }
  else if (!animated){
    if (toggle_X) res.x = map(result, -1, 1, 0, x_Slider);
    if (toggle_Y) res.y = map(result, -1, 1, 0, y_Slider);
    if (toggle_Z) res.z = map(result, -1, 1, 0, z_Slider);
  }

  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;
}

PVector floorMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,50,0);

  int numFrames = numFrames_Slider;
  float radius = radius_Slider;
  float scale = scale_Slider;
  float t = 1.0 * i / numFrames;

  float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

  float rippler = map(sin(t), -1, 1, 170, 85);

  float rad = map(sin(t), -1, 1, -mod1, mod1);
  float spiroDist = dist(res.x, res.y, position.x + map(sin(t), -1, 1, -rad, rad), position.y + map(cos(t), -1, 1, rad, -rad));
  float noiseDist = dist(res.x, res.y, position.x + map(ns, -1, 1, -rad * 2, rad * 2), position.y + map(ns, -1, 1, rad * 2, -rad * 2));
  float result = 100 / (rippler + spiroDist);

  if (animated){
    if (toggle_Z) res.z += map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_X) res.x += map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y += map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
  }
  else if (!animated){
    if (toggle_X) res.x += map(result, -1, 1, x_Slider, 0);
    if (toggle_Y) res.y += map(result, -1, 1, y_Slider, 0);
    if (toggle_Z) res.z += map(result, -1, 1, z_Slider, 0);
  }


  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;
}

PVector headMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  int numFrames = numFrames_Slider;
  float radius = radius_Slider;
  float scale = scale_Slider;
  float t = 1.0 * i / numFrames;

  float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

  float rippler = map(ns, -1, 1, -10, 10);
  float rad = map(sin(ns), -1, 1, -1, 1);
  float spiroDist = dist(res.x, res.y, position.x + map(sin(t), -1, 1, -rad, rad), position.y + map(cos(t), -1, 1, rad, -rad));
  float noiseDist = dist(res.x, res.y, position.x / map(ns, -1, 1, -rad/2, rad/2), position.y / map(ns, -1, 1, rad/2, -rad/2));
  float result = 300 / (rippler + noiseDist);

  if (animated){
    if (toggle_Z) res.z += map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_X) res.x += map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y += map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
  }
  else if (!animated){
    if (toggle_X) res.x = map(result, -1, 1, -x_Slider, x_Slider);
    if (toggle_Y) res.y = map(result, -1, 1, -y_Slider, y_Slider);
    if (toggle_Z) res.z = map(result, -1, 1, -z_Slider, z_Slider);

  }

  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;


}

PVector sourMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  float t = 1.0 * i / numFrames_Slider;

  // Generate a looping noise value
  float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

  float poker = map(ns, -1, 1, mod1,mod2);
  float rad = map(ns, -1, 1, -10, 10);
  float noiseDist = dist(res.x, res.y, position.x + ns , position.y + ns);
  float result = 128.0 * ns / noiseDist;

  if (animated){
    if (toggle_X) res.x = map2(result, 0.8, 2, xMin/20, xMax/20, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y = map2(result, 0.8, 2, yMin/20, yMin/20, QUADRATIC, EASE_IN_OUT);
    if (toggle_Z) res.z = map2(result, 0.8, 2, zMin/20, zMin/20, QUADRATIC, EASE_IN_OUT);
  }
  else{
    if (toggle_X) res.x = map(result, 0.8, 2, -x_Slider, x_Slider);
    if (toggle_Y) res.y = map(result, 0.8, 2, -y_Slider, y_Slider);
    if (toggle_Z) res.z = map(result, 0.8, 2, -z_Slider, z_Slider);
  }

  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;
}

PVector bulgeMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  float t = 1.0 * i / numFrames_Slider;

  float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
  float poker = map(sin(ns), -1, 1, 0, mod2);
  float centreDist = dist(position.x + map(ns, -1, 1, -mod1, mod1), position.y + map(ns, -1, 1, -mod1, mod1), res.x, res.y);
  float result = 100 / (poker + centreDist);

  if (animated){
    if (toggle_Z) res.z += map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_X) res.x += map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y += map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
  }
  else if (!animated){
    if (toggle_X) res.x = map(result, 0, 3, -x_Slider, x_Slider);
    if (toggle_Y) res.y = map(result, 0, 3, -y_Slider, y_Slider);
    if (toggle_Z) res.z = map(result, 0, 3, -z_Slider, z_Slider);
  }
  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;
}

PVector depressing(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    int numFrames = numFrames_Slider;
    float radius = radius_Slider;
    float scale = scale_Slider;
    float t = 1.0 * i / numFrames;

    float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

    float rippler = map(ns, -1, 1, 0, mod1);
    float rad = map(ns, -1, 1, -mod2, mod2);
    float spiroDist = dist(res.x, res.y, position.x + map(sin(t), -1, 1, -rad, rad), position.y + map(cos(t), -1, 1, rad, -rad));
    float noiseDist = dist(position.x, position.y, res.x + map(sin(ns), -1, 1, -rad, rad), res.y + map(cos(ns), -1, 1, -rad, rad));
    float result = 100 / (rippler + noiseDist);

    if (animated){
      if (toggle_Z) res.z += map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
      if (toggle_X) res.x += map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
      if (toggle_Y) res.y += map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
    }
    else if (!animated){
      if (toggle_Z) res.z += map2(result, -1, 1, -z_Slider, z_Slider, QUADRATIC, EASE_IN_OUT);
      if (toggle_X) res.x += map2(result, -1, 1, -y_Slider, y_Slider, QUADRATIC, EASE_IN_OUT);
      if (toggle_Y) res.z += map2(result, -1, 1, -y_Slider, y_Slider, QUADRATIC, EASE_IN_OUT);
    }

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
  }

} // End of Vert class
