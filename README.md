# dock-doors
Rest Webservice and Google Cloud Spanner Spring Boot Project


The 3 steps necessary to get a Node app running in Kubernetes are:
Use Docker to create a container image of your application
Store the image in GCP's Container Registry
Create a deployment of your image in Kubernetes
Using the above Node application, create a Dockerfile that will allow you to build a runnable image it. (If you don't have Docker installed and running on your machine, install it and run it before continuing.) In the same directory as the package.json:  touch Dockerfile . Add the following contents to the Dockerfile.

FROM mhart/alpine-node
WORKDIR /src
COPY package.json .
RUN npm install
COPY . .
EXPOSE 8000
CMD ["node", "index.js"]

Once the file is added, you will be able to build the container image by running  docker build -t gcr.io/<PROJECT-ID>/my-little-app:v1 .  where <PROJECT-ID> cooresponds to your project. Once the image is done building, you should be able to see your image after it's built by running:  docker images .
Locally test-run the image by executing: docker run -p 8000:8000 -t gcr.io/<PROJECT-ID>/my-little-app:v1 and then visiting http://localhost:8000/randomNumber
Now we need to get it into Kubernetes. Confirm that your gcloud shell command is "pointed" at your own GCP project. By running gcloud init, you will be prompted to log-in to your GCP account and verify you want to connect.
If you have not yet created a cluster for your account, go to your GCP Console, navigate to Kubernetes Engine, then Kubernetes Clusters, and create a cluster. Give it any name you like, such as "bootcamp-cluster". (Note it cannot contain capital letters). Choose settings for your cluster that are minimal. The more memory or CPUs you allocate, the faster you will spend the $300 provided for the trial account.
Taking note of your Project ID, Project Name, Cluster Name and Cluster Zone, use the following commands to prepare your terminal to push the deployment:
gcloud config set project <PROJECT-ID>
gcloud config set compute/zone <CLUSTER-ZONE>
gcloud config set container/use_v1_api_client false
gcloud beta container clusters get-credentials <CLUSTER-ID> --region <CLUSTER-ZONE>
Now, in order to deploy your app, you need to add a deployment yaml to your project. A deployment yaml will tell Kubernetes your expectations for your application and then it will try and meet those expectations. Expectations in this sense would be like the number of instances, the amount of memory it is allowed to be allocated, and the number of CPUs it can have. Any time the application is using resources or behaving in a way that is not meeting the set expectations, Kubernetes will take actions to fix the state. Create a file called  deployment.yml  in the root of your project directory and add the following contents:

  apiVersion: extensions/v1beta1
  kind: Deployment
  metadata:
    name: my-little-app  #Your app's name
  spec:
    replicas: 1
    template:
      metadata:
        labels:
          app: my-little-app
      spec:
        containers:
        - name: my-little-app  #Your app will be the running container you built
          image: gcr.io/<PROJECT-ID>/my-little-app:v1
          imagePullPolicy: Always
  ---
  kind: Service
  apiVersion: v1
  metadata:
    name: my-little-app-load-balancer   #This will be the name of your load balancer
  spec:
    selector:
      app: my-little-app  #The load balancer will be attached to the app you specify by name here
    ports:
    - protocol: TCP
      port: 80
      targetPort: 8000
    type: LoadBalancer
    loadBalancerSourceRanges:  #Firewall rules
    - 151.140.0.0/16
    - 165.130.0.0/16
    - 207.11.0.0/17
    - 50.207.27.182/32
    - 98.6.11.8/29
    
Now, assuming that your Docker image built and ran correctly, your  gcloud  is configured to your GCP project, and you've replaced all occurances of <PROJECT-ID> with your actual Project-ID in the instructions above, we should be ready to deploy your Node.js app to Kubernetes.
First, push your Docker image to GCP using this command:  gcloud docker -- push gcr.io/<PROJECT-ID>/my-little-app:v1. Next, push your deployment yaml to Kubernetes, which instructs Kubernetes to run the image you just pushed and make it available through a load balancer:  kubectl apply -f ./deployment.yml . Now, you should be able to go to your Kubernetes console in GCP, under "workloads" and see your application running. Also, go to the "discovery and load balancing" menu of Kubernetes. You should see your load balancer there with an external i.p address. Visiting that address and the endpoint /randomNumber, you will be able to access your running application.


Service accounts are used to authenticate a machine and allow that computer to interact (read, write, or delete) with a secure resource. The most notable use of service accounts is to protect access to Spanner databases.
To create a service account, go to your cloud console and open the menu. In the first section, open the sub-menu called IAM & admin then click Service accounts. At the top of the page, click CREATE SERVICE ACCOUNT.
A small menu should open in the center of the page. Here you will select all of the permissions to give this particular service account. A service account can have one or many permissions associated with it. You can give it access to Spanner, Cloud SQL, Pub/Sub, Kubernetes, and any other resource on the platform. For this tutorial, we will only focus on Cloud Spanner.
Name your service account spanner_read_write.
screenshot
Download the file as a json by opening the menu on the right side of the page, in the row that represents your new service account. Press Create Key. You can rename the file after downloading, if you'd like. The file will be in your "Downloads" folder.
menumenu
In your Spring Boot or Node project, if you need access to Spanner, you will have to use this file to enable the library during run-time. Do this by setting an environment variable called  GOOGLE_APPLICATION_CREDENTIALS . The value for this environment variable will be the absolute path of the service account json file. In Intelli-J, you can edit your environment variables by using the "Edit Configurations" menu option. (This will only work when running locally.)
To deploy your code to Kubernetes and still have your application retain access to your Spanner instance, you will have to create a SECRET using the service account json you just downloaded.
Initialize your  gcloud  your project using  gcloud init . Also, make sure  kubectl  is installed. (See Deploy To Kubernetes tutorial for details.).
To create the secret run the following command using the absolute file path of your json file:
 kubectl create secret generic NAME-OF-YOUR-SECRET --from-file=YOUR-CREDENTIAL-FILE-PATH 
An Actual command would look something like this:
kubectl create secret generic spanner-secret --from-file=/Users/aba/Downloads/service-acct-1822.json
If you are unable to execute this command because of a connection problem, go to your clusters, press the CONNECT button on the right and run the command that is given.
You can verify your secret was created by going to the Configuration menu in the Kubernetes section of GCP.
Now that your secret exists, add it to your deployment.yml file.
To add the secret to your application, you will add a volume to your deployment. A volume is a local file-system that will be accessible to your app while it's running. Just as you enabled your appication to read a file from your computer when you were running locally, you will be enabling your application to read a file from Kubernete's file system when it is running in the cloud. You have already saved the file to that file system in the cloud by creating a secret, now we need to mount storage (volumes) to your deployment so it can be read at run-time.
The underlined sections are what you should be adding to your yml. The other sections were already there if you did the previous tutorials. The blue sections will require information about your applicaiton. You put your app's info there.
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: NAME-OF-YOUR-APPLICATION
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: NAME-OF-YOUR-APPLICATION
    spec:
      volumes:
        - name: spanner-creds
          secret:
            secretName: NAME-OF-YOUR-SECRET
      containers:
      - name: NAME-OF-YOUR-APPLICATION
        image: YOUR-DOCKER-IMAGE-NAME
        ports:
          - containerPort: 8080
            name: app
        env:
          - name: GOOGLE_APPLICATION_CREDENTIALS
            value: /secrets/app-secrets/NAME-OF-YOUR-SVC-ACCT-JSON-FILE
        volumeMounts:
          - name: spanner-creds
            mountPath: /secrets/app-secrets
            readOnly: true
        imagePullPolicy: Always
---
kind: Service
apiVersion: v1
metadata:
  name: NAME-OF-YOUR-APPLICATION
spec:
  selector:
    app: NAME-OF-YOUR-APPLICATION
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
  loadBalancerSourceRanges:
  - 151.140.0.0/16
  - 165.130.0.0/16
  - 207.11.0.0/17
  - 50.207.27.182/32
  - 98.6.11.8/29
Here's an example of one that is completely filled out:
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: my-little-app
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: my-little-app
    spec:
      volumes:
        - name: spanner-creds
          secret:
            secretName: spanner-secret   # This will match the name you gave your secret when you created it 
      containers:
      - name: my-little-app
        image: gcr.io/dock-doors/dock-door:v1
        imagePullPolicy: Always
        env:
          - name: GOOGLE_APPLICATION_CREDENTIALS
            value: /secrets/app-secrets/Dock-doors-64c1bc17b437.json
        volumeMounts:
          - name: spanner-creds
            mountPath: /secrets/app-secrets
            readOnly: true
---
kind: Service
apiVersion: v1
metadata:
  name: my-little-app-load-balancer
spec:
  selector:
    app: my-little-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
  loadBalancerSourceRanges:
  - 151.140.0.0/16
  - 165.130.0.0/16
  - 207.11.0.0/17
  - 50.207.27.182/32
  - 98.6.11.8/29
  
Now, you should be able to use  kubectl apply -f deployment.yml  as usual to see your application deploy. Verify your application deployed with your secrets by going to your deployment in Kubernetes and seeing a section called Configuration at the bottom. (Note, the secret in the image is not named 'spanner-creds', but yours should be.)
menu