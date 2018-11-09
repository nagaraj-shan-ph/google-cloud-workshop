#!/bin/sh

# Set the metadata server to the get projct id
PROJECTID=$(curl -s "http://metadata.google.internal/computeMetadata/v1/project/project-id" -H "Metadata-Flavor: Google")
BUCKET=$(curl -s "http://metadata.google.internal/computeMetadata/v1/instance/attributes/BUCKET" -H "Metadata-Flavor: Google")
SQL_INSTANCE=$(curl -s "http://metadata.google.internal/computeMetadata/v1/instance/attributes/SQL_INSTANCE" -H "Metadata-Flavor: Google")

echo "Project ID: ${PROJECTID} Bucket: ${BUCKET}"

# Get the files we need
gsutil cp gs://${BUCKET}/demo.jar .

# Install dependencies
sudo apt-get update
sudo apt-get -y --force-yes install openjdk-8-jdk

# Make Java 8 default
sudo update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

# Download Cloud Proxy and make it executable
wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O cloud_sql_proxy
chmod +x cloud_sql_proxy

# Create Directory for Cloud Sql Proxy Connection
mkdir cloudsql; sudo chmod 777 cloudsql

service_name=clms
sudo mkdir -p /var/log/${service_name}
sudo touch /var/log/${service_name}/app.log
sudo touch /var/log/${service_name}/${service_name}-stderr.log
sudo chown -R $USER:$USER /var/log/${service_name}

export APPLICATION_STORAGE_BUCKET="${BUCKET}"

# Start the Cloud SQL Proxy in the background
./cloud_sql_proxy -dir=/cloudsql -instances=${SQL_INSTANCE}=tcp:5432 &

# Start server
java -jar demo.jar