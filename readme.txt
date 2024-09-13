This is a PoC project to make web-service available for Stellantis in CapitalX.
Provides the following features:
* Release Level change in design in Capital.
* Triggers this web service hook.
* Reads chs_cust.properties.
* Set the realtive path for the task to write files into directory.
* Submit custom Task into cis-worker, this may include exporting a pdf as well.
* Task completion, upload the files into S3 bucket.

STEPS TO RUN
* Make sure you have ANT in local system.
* Run ant clean , if not first time doing build.
* If first time build, run "ant build -verbose".
* You should see a folder created "ServerWebApp".
* Configure chs_cust.properties inside DServerWebApp\WEB-INF\config:
	* CIS_SVG_OUTPUT=output/Ashutosh/CustomTask (Can be pointed to a relative path for S3-Bucket)
	* NOTIFY_SVG_TIF_PATH=output/Ashutosh/CustomTask  (Can be pointed to a relative path for S3-Bucket)
	* SCRIPT_PATH=D:/fem-python-configmaps/fem_script.py (Where the python script is lcoated to invoke the postProcessing to upload files to S3.)
* Navigate inside the ServerWebApp/WEB-INF, from command line start webserver.bat.
* It will start the web-service at 3113.