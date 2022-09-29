# Inviso3D

[![Screen capture video](https://raw.githubusercontent.com/rgombash/inviso/master/inviso_screenshot.png)](https://youtu.be/b5nJMbBpMWQ "Screen capture video")

### Idea, Vision

Expanding perception and visibility of complex distributed computing systems through spatial visualization.

### What does it do currently ?

Gets the list of objects from Kubernetes and displays it in 3d space with meta info embedded in visual representation.
In the ProxyService, there are still classes to fetch data from GC Compute and OpenShift, but in the current branch, the focus is on Kubernetes.

### Architecture

[3d/2d client(s)] <--websocket--> [proxy service] <--http(s), kafka, ...---> [data source(s)]

Client/server communication is done via simple JSON asynchronously.

### Proxy Service

ProxyService is an intermediary that proxies data from K8s API. It provides a WebSocket and HTTP server and keeps track of client sessions.
It is written in Java and Spark framework. Currently has Kubernetes, OpenShift, and GCP Compute data sources (plugins), where the focus is on Kubernetes.
Apart from the WebSocket endpoint service also has an HTTP endpoint for static content.
By default, the service runs on port 4567 (both HTTP and WS)

### Proxy Service Plugin 

##### Kubernetes

For the plugin to work, you need to have either:
- Configured kubectl on the machine where Inviso ProxyService will run or config file with at least one context. On Linux, usually in ~/.kube/config.
- If deployed using Helm, it will pick the config from within the cluster (Service account RO access rights defined in Helm needs to be applied during installation)

##### OpenShift Plugin [Legacy]

Gets and filters pods from OpenShift API. The plugin was tested with OKD v3.11 with API v1.
Currently only works with token auth. 
To get your API key, use oc tool: 

`oc login -u your_username https://url_to_your_openshift.com:8443`

`oc whoami -t` 

Update config.properties accordingly.

##### Google Cloud Compute Plugin [Legacy]

Uses Google's API Client for Java and gcloud CLI authentication.

Howto for installing the gcloud command line tool: https://cloud.google.com/sdk/docs/quickstarts
After setting it up and successfully authenticating, you need you set the environment variable GOOGLE_APPLICATION_CREDENTIALS to point to the gcloud credentials token.

In Linux/bash:

`export GOOGLE_APPLICATION_CREDENTIALS = /home/YOUR_HOME_DIR/.config/gcloud/legacy_credentials/YOUR_USER@SOMEWHERE.COM/adc.json`

Or use your preferred IDE to set up runtime environment variable.   

### Visualisation Clients

All static content (clients) reside in `/src/main/resources/public/`
Service serves this content on http://localhost:4567 (if you are running the service locally)

#### 3DView 

Technology : html, .js, three.js (WebGL)

3D representation of nodes. Each box represents one node/container/VM where position represents state (higher = online / lower = offline)
Colors represent different roles. Aiming and clicking on a specific box shows basic info in the properties box.

If run locally, you can access it on: http://localhot:4567/3dview.html

The current configured frontend is 3dviewV2.js, and it supports only Kubernetes visualization

* Kubernets query string paramaters: http://localhost:4567/3dview.html?provider=k8s&context=YOUR-K8S-CONTEXT

Inspiration for the 3Dviewer was [three.js](https://threejs.org/) example: (https://threejs.org/examples/#misc_controls_pointerlock)

#### Debug endpoint for testing server messages

location `/debug.html`

Displays server (ProxyService) messages and active clients

### Quickstart: Build and Run

Tested with JDK 9 and OpenJDK 9 

##### Update service config file

Copy dist.config.properties to config.properties and update authentication and config data.

`cp dist.config.properties config.properties`

##### Maven build

`mvn clean compile assembly:single`

##### Run

`java -cp target/inviso-1.0-SNAPSHOT-jar-with-dependencies.jar ProxyService`

If running locally you should go to http://localhost:4567 for index page

##### Running in Docker

Get the repository and change directory
`git clone git@github.com:rgombash/inviso3d.git & cd inviso`

`cp dist.config.properties config.properties`
Edit config.properties and update authentication and config data according to your setup
Note: For viewing GC Compute or Kubernetes, you must get your configs into the container image by adding them to Dockerfile or using the 'docker cp' command. Refer to the plugins section for details.

Build the image
`docker build -t inviso .`

Run the image
`docker run -p 4567:4567 inviso`

##### Running in Kubernetes cluster
dependencies: installed and configured kubectl and helm 

from the source root:

`cd charts`
`helm install inviso ./inviso`

### TODOs
* Enable HTTPS and WSS
* Add more object types deployments, replica sets, services etc. 
* Test subscription model (client subscribes to periodic / streaming updates)

### Final notes
Inviso3D is a hobby project that I have been playing with sporadically for a few years now. The initial concept was written in Python and the Panda3d engine and was later rewritten to Java and Three.js engine.
The code was prototyped fast and dirty (also my first Java service), so do not expect high-quality code.
