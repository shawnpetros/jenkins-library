package com.sap.piper.mta

class MtaMultiplexer implements Serializable {
    static Map createJobs(Script step, Map parameters, List excludeList, String jobPrefix, String buildDescriptorFile, String scanType, Closure worker) {
        Map jobs = [:]
        def filesToScan

        filesToScan = step.findFiles(glob: "**${File.separator}${buildDescriptorFile}")
        step.echo "Found ${filesToScan.length} ${scanType} descriptor files!"
        filesToScan = removeExcludedFiles(step, filesToScan, excludeList)

        for (String file : filesToScan){
            def options = [:]
            options.putAll(parameters)
            options.scanType = scanType
            options.buildDescriptorFile = file
            jobs["${jobPrefix} - ${file.replace("${File.separator}${buildDescriptorFile}",'')}"] = {worker(options)}
        }
        return jobs
    }

    static def removeExcludedFiles(Script step, filesToScan, List filesToExclude){
        def filteredFiles = []
        for (File file : filesToScan) {
            if(filesToExclude.contains(file.path)){
                step.echo "Skipping ${file.path}"
            }else{
                filteredFiles.add(file.path)
            }
        }
        return filteredFiles
    }
}
