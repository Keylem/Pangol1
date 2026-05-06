package fr.univrennes.istic.l2gen.application.core.services;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class ObjectServiceTest {

    private final List<File> tempFiles = new ArrayList<>();

    @After
    public void cleanup() {
        for (File file : tempFiles) {
            if (file != null && file.exists()) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                }
            }
        }
    }

    private File createTempSerFile(String prefix) throws IOException {
        File file = File.createTempFile(prefix, ".ser");
        tempFiles.add(file);
        return file;
    }

    private File createTempDirectory(String prefix) throws IOException {
        File directory = Files.createTempDirectory(prefix).toFile();
        tempFiles.add(directory);
        return directory;
    }

    private static class TestData implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String value;
        private final int number;

        TestData(String value, int number) {
            this.value = value;
            this.number = number;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestData)) {
                return false;
            }

            TestData other = (TestData) obj;
            return value.equals(other.value) && number == other.number;
        }
    }

    @Test
    public void testSaveAndLoadSimpleObject() throws Exception {
        File tempFile = createTempSerFile("object-service");
        TestData original = new TestData("test", 42);

        ObjectService.save(tempFile, original);
        Optional<TestData> loaded = ObjectService.load(tempFile, TestData.class);

        Assert.assertTrue(tempFile.exists());
        Assert.assertTrue(tempFile.length() > 0);
        Assert.assertTrue(loaded.isPresent());
        Assert.assertEquals(original, loaded.get());
    }

    @Test
    public void testLoadReturnsEmptyForMissingFile() {
        File missingFile = new File(System.getProperty("java.io.tmpdir"),
                "object-service-missing-" + System.nanoTime() + ".ser");

        Optional<TestData> loaded = ObjectService.load(missingFile, TestData.class);

        Assert.assertFalse(loaded.isPresent());
    }

    @Test
    public void testLoadReturnsEmptyForWrongType() throws Exception {
        File tempFile = createTempSerFile("object-wrong-type");
        ObjectService.save(tempFile, new TestData("test", 42));

        Optional<ArrayList> loaded = ObjectService.load(tempFile, ArrayList.class);

        Assert.assertFalse(loaded.isPresent());
    }

    @Test
    public void testSaveAndLoadList() throws Exception {
        File tempFile = createTempSerFile("object-list");

        ArrayList<TestData> originalList = new ArrayList<>();
        originalList.add(new TestData("first", 1));
        originalList.add(new TestData("second", 2));
        originalList.add(new TestData("third", 3));

        ObjectService.save(tempFile, originalList);
        Optional<ArrayList> loaded = ObjectService.load(tempFile, ArrayList.class);

        Assert.assertTrue(loaded.isPresent());
        Assert.assertEquals(3, loaded.get().size());
    }

    @Test
    public void testSaveAndLoadTwoIndependentFiles() throws Exception {
        File file1 = createTempSerFile("object-v1");
        File file2 = createTempSerFile("object-v2");

        ObjectService.save(file1, new TestData("version1", 1));
        ObjectService.save(file2, new TestData("version2", 2));

        Optional<TestData> loaded1 = ObjectService.load(file1, TestData.class);
        Optional<TestData> loaded2 = ObjectService.load(file2, TestData.class);

        Assert.assertTrue(loaded1.isPresent());
        Assert.assertTrue(loaded2.isPresent());
        Assert.assertEquals("version1", loaded1.get().value);
        Assert.assertEquals("version2", loaded2.get().value);
    }

    @Test
    public void testLoadReturnsEmptyForNullPath() {
        Optional<TestData> loaded = ObjectService.load(null, TestData.class);
        Assert.assertFalse(loaded.isPresent());
    }

    @Test
    public void testSaveAndLoadEmptyList() throws Exception {
        File tempFile = createTempSerFile("object-empty-list");

        ArrayList<TestData> emptyList = new ArrayList<>();
        ObjectService.save(tempFile, emptyList);

        Optional<ArrayList> loaded = ObjectService.load(tempFile, ArrayList.class);

        Assert.assertTrue(loaded.isPresent());
        Assert.assertTrue(loaded.get().isEmpty());
    }

    @Test
    public void testLoadReturnsEmptyAfterFileDeletion() throws Exception {
        File tempFile = createTempSerFile("object-delete");

        ObjectService.save(tempFile, new TestData("test", 42));
        Files.deleteIfExists(tempFile.toPath());

        Optional<TestData> loaded = ObjectService.load(tempFile, TestData.class);

        Assert.assertFalse(loaded.isPresent());
    }

    @Test
    public void testSaveToDirectoryDoesNotThrow() throws Exception {
        File tempDir = createTempDirectory("object-dir");

        ObjectService.save(tempDir, new TestData("test", 42));

        Assert.assertTrue(tempDir.exists());
        Assert.assertTrue(tempDir.isDirectory());
    }

    @Test
    public void testSaveAndLoadUsingOverloadsWithoutPath() {
        TestData expected = new TestData("scoped", 7);
        File scopedFile = new File(FileService.getAppDataDir(), TestData.class.getSimpleName() + ".ser");
        tempFiles.add(scopedFile);

        ObjectService.save(expected);
        Optional<TestData> loaded = ObjectService.load(TestData.class);

        Assert.assertTrue(loaded.isPresent());
        Assert.assertEquals(expected, loaded.get());
    }
}
