# Simulacra

### MA Computational Art Final Project 2018.

Simulacra is a series of procedurally generated digital artefacts which aestheticise the body through fleshy materials and intestinal textures. By exploring the human connection with the soft and somatic, Simulacra embraces the grotesque, where once rigid non-human forms bulge and undulate in seamless and satisfyingly endless loops. 

## Getting Started

### simulacraMaster

A Processing sketch to playfully create looping 3D animations with simplex noise. Simply place the folder in your Processing working folder and build.

### simulacraViewer

An openFrameworks app for autonomously presenting the rendered works in an exhibition environment via Raspberry Pi. To run, make sure the openFrameworks examples compile correctly on the Pi, then simply copy the contents of ofApp.h, ofApp.cpp and Main.h into a new project. 

## Dependencies

### simulacraMaster

* [Processing](https://processing.org/) - Used for simulacraMaster
* [peasyCam](http://mrfeinberg.com/peasycam/) - To be able to manipulate the camera
* [controlP5](http://www.sojamo.de/libraries/controlP5/) - For GUI control
* [Nervous System OBJ Exporter](https://n-e-r-v-o-u-s.com/tools/obj/) - For exporting OBJ sequences
* [Toxiclibs](http://toxiclibs.org/2010/02/simutils-grayscott/) - Reaction diffusion algorithm.
* [Blender](https://www.blender.org/) - Used to render the OBJ sequences
* [Stop-motion-OBJ](https://github.com/neverhood311/Stop-motion-OBJ) - For importing OBJ sequences into Blender

### simulacraViewer

* [openFrameworks 0.9.8](https://openframeworks.cc/) - Used to build simulacraViewer
* [ofxOMXPlayer](https://github.com/jvcleave/ofxOMXPlayer) - For smoothly playing back video on Raspberry Pi
* [Raspberry Pi](https://www.raspberrypi.org/) - Allows for autonomous playback during installation
 
## Running simulacraMaster

First select a shape to create in setup() by setting the corresponding boolean to 'true'

```
hyperbolic = true;
```

Select an update function in the Vert class by uncommenting the function desired.

```
position = PVector.add(initPosition, getSimplexMod(i) );
```

Play with the GUI controls in order to create interesting behaviours

## Running simulacraViewer

The video file to be looped should be placed in;

```
/data/movies/
```

This path can be changed in ofApp.h if desired.


The Raspberry Pi can be configured to run the app on startup by adding the app’s path to the Pi’s config file, for example;

```
cd openFrameworks/apps/myApps/simulacraViewer
make RunRelease
```

Note this will not compile in OSX.


## Contributing

Feel free to reach out if you are interested in contributing.

## Authors

* **Lior Ben Gai** - *Initial boilerplate*
* **Joseph Rodrigues Marsh** - *Ongoing development*

## Acknowledgments

* Lior Ben Gai for the initial boilerplate
* Etienne Jacob for the looping simplex noise algorithm
* Daniel Shiffman for meshing 3D spherical geometry
