package fr.univrennes.istic.l2gen.application.core.services;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class FileServiceTest {

    @Test
    public void testGetExtensionCsv() {
        File file = new File("data.csv");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("csv", ext);
    }

    @Test
    public void testGetExtensionTsv() {
        File file = new File("data.tsv");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("tsv", ext);
    }

    @Test
    public void testGetExtensionParquet() {
        File file = new File("data.parquet");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("parquet", ext);
    }

    @Test
    public void testGetExtensionZip() {
        File file = new File("archive.zip");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("zip", ext);
    }

    @Test
    public void testGetExtensionUppercase() {
        File file = new File("DATA.CSV");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("csv", ext);
    }

    // Cas limite
    @Test
    public void testGetExtensionNoExtension() {
        File file = new File("README");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("", ext);
    }

    @Test
    public void testGetExtensionWithPath() {
        File file = new File("/path/to/data.csv");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("csv", ext);
    }

    @Test
    public void testGetExtensionMultipleDots() {
        File file = new File("data.backup.csv");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("csv", ext);
    }

    @Test
    // check si le chemin absolu est bien pangol1
    public void testGetAppDataDirContainsPangol1FolderName() {
        File appDataDir = FileService.getAppDataDir();
        Assert.assertNotNull(appDataDir);
        Assert.assertTrue(appDataDir.getAbsolutePath().contains(".Pangol1"));
    }

    @Test

    public void testGetAppDataDirReturnsExistingDirectory() {
        File appDataDir = FileService.getAppDataDir();
        Assert.assertNotNull(appDataDir);
        Assert.assertTrue(appDataDir.exists());
        Assert.assertTrue(appDataDir.isDirectory());
    }

    @Test
    public void testGetAppDataDirStableBetweenCalls() {
        File firstCall = FileService.getAppDataDir();
        File secondCall = FileService.getAppDataDir();

        Assert.assertEquals(firstCall.getAbsolutePath(), secondCall.getAbsolutePath());
    }

    @Test
    public void testGetExtensionEdgeCaseEmptyString() {
        File file = new File("");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("", ext);
    }

    @Test
    public void testGetExtensionDotOnly() {
        File file = new File(".");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("", ext);
    }

    @Test
    public void testGetExtensionHiddenFile() {
        File file = new File(".hidden");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("", ext);
    }

    @Test
    public void testGetExtensionTxt() {
        File file = new File("notes.txt");
        String ext = FileService.getExtension(file);
        Assert.assertEquals("txt", ext);
    }

}
