package io.github.robwin.swagger2markup.tasks
import io.telekom.gradle.plugin.rms.http.SessionFacade
import io.telekom.gradle.plugin.rms.model.UploadItem
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import org.gradle.mvn3.org.codehaus.plexus.util.StringUtils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Swagger2MarkupTask extends DefaultTask {

    private static final int MD5_NUMBEROF_DIGITS = 32;

    @Input
    URL uploadUrl

    @Input
    String username

    @Input
    String password

    @InputDirectory
    File distributionsDir

    @Input
    String artifactVersion

    @Input
    String artifactDescription

    @Input
    int serviceId

    @Input
    int platformId

    @Input
    boolean dryRun

    @TaskAction
    void upload() {
        logger.debug("Upload started")
        if (logger.isInfoEnabled()) {
            printConfiguration()
        }
        def sessionFacade = new SessionFacade(getUploadUrl(), getUsername(), getPassword())
        try {
            sessionFacade.login()
            def platforms = sessionFacade.loadPlatformList()
            def services = sessionFacade.loadSystemList()
            sessionFacade.upload(Arrays.asList(createUploadItem()), null, getDryRun(), platforms, services)
        } catch (Exception e) {
            throw new GradleScriptException("RmsUploadTask execution failed", e)
        } finally {
            try {
                sessionFacade.logout()
            } catch (Exception e) {
                logger.error("Unable to logout", e)
            }
        }
        logger.trace("Upload finished")

    }

    public void printConfiguration() {
        logger.info("UploadUrl: ${getUploadUrl()}")
        logger.info("Username: ${getUsername()}")
        logger.info("Password: ${getPassword()}")
        logger.info("DistributionsDir: ${getDistributionsDir()}")
        logger.info("Description: ${getArtifactDescription()}")
        logger.info("Version: ${getArtifactVersion()}")
        logger.info("ServiceId: ${getServiceId()}")
        logger.info("PlatfromId: ${getPlatformId()}")
    }

    private UploadItem createUploadItem() {
        PatternSet patternSet = new PatternSet().include('**/*.rpm', '**/*.RPM')
        File rpmFile = project.fileTree(getDistributionsDir()).matching(patternSet).singleFile
        if (logger.isInfoEnabled()) {
            logger.info("Artifact: ${rpmFile.absoluteFile}")
        }
        def item = new UploadItem()
        item.setPath(rpmFile.absoluteFile)
        item.setPlatformId(getPlatformId())
        item.setServiceId(getServiceId())
        item.setVersion(getArtifactVersion())
        item.setDescription(getArtifactDescription())
        item.setFileId(UUID.randomUUID().toString())
        item.setMd5(calcMD5(item.getPath()))
        return item
    }

    private static String calcMD5(File f) throws IOException, NoSuchAlgorithmException {
        InputStream is = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5")
            is = new FileInputStream(f)
            byte[] buffer = new byte[8192]
            int read
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read)
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String md5String = bigInt.toString(16)
            //Using bigInteger for conversion from byte[] the leading zeroes are removed
            //So we need to pad left of the md5 String with zeroes to the md5 length of 32 digits
            md5String = StringUtils.leftPad(md5String, MD5_NUMBEROF_DIGITS, '0')
            return md5String;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
