package samwang.anki.service;

import com.samwang.anki.impl.AnkiFileLogger;
import com.samwang.anki.impl.AnkiFileWriter;
import com.samwang.anki.impl.MarkdownImporter;
import com.samwang.anki.impl.PreProcessor;
import com.samwang.anki.impl.model.CardGroup;
import com.samwang.anki.impl.model.OutputLogCollector;
import com.samwang.anki.impl.model.TokenCard;
import com.samwang.anki.impl.model.token.MetaInfoType;
import samwang.anki.model.CardDTO;
import samwang.anki.model.CardGroupDTO;
import samwang.anki.model.OutputDTO;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PreviewService {

    private static final String loggerPropName = ".anki-imp-config.properties";
    private final File rootDir;

    public PreviewService(String root) {
        rootDir = new File(root);
        if (!rootDir.exists()) throw new RuntimeException("Root dir not existing:" + root);

    }

    private AnkiFileLogger loadPropLogger() {
        File loggerProp = new File(rootDir, loggerPropName);
        if (loggerProp.exists()) {
            System.out.println("logger file:" + loggerProp.getAbsolutePath());
            return new AnkiFileLogger(loggerProp);
        } else {
            System.out.println("No logger file");
            return AnkiFileLogger.dummyLogger();
        }
    }

    public File getRootDir() {
        return rootDir;
    }

    public List<String> getFiles() {
        return Arrays.asList(rootDir.list(fileNameFilter()));
    }

    public int clearFiles() {
        File[] files = rootDir.listFiles(fileNameFilter());
        int i = 0;
        for (File file : files) {
            if (file.delete()) i++;
        }
        return i;
    }

    private FilenameFilter fileNameFilter() {
        return (dir, name) -> name.endsWith(".md");
    }

    public File beforeSave(File file) {
        if (file.exists()) return null;
        return file;
    }

    public List<CardGroupDTO> getGroups(String uploadedFileName, String question) throws IOException {
        return _getGroups(uploadedFileName, loadPropLogger(), question, true)
            .map(x -> new CardGroupDTO(x))
            .collect(Collectors.toList());
    }

    public void outputGroups(String uploadedFileName, OutputLogCollector collector, String question) throws IOException {
        AnkiFileLogger fileLogger = loadPropLogger();
        try {
            List<CardGroup> groups = _getGroups(uploadedFileName, fileLogger, question, false).collect(Collectors.toList());
            outputGroups(groups, collector);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new IOException(e);
        } finally {
            fileLogger.close();
        }
    }

    public void outputGroups(OutputDTO outputCommand, OutputLogCollector collector) throws IOException {
        AnkiFileLogger fileLogger = loadPropLogger();

        Function<String, Stream<CardGroup>> getGroupsOrError = file -> {
            try {
                return _getGroups(file, fileLogger, outputCommand.getQuestion(), false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        try {
            Stream<CardGroup> groupStreams = outputCommand.getFiles().stream()
                .map(f -> getGroupsOrError.apply(f))
                .reduce((accu, item) -> Stream.of(accu, item).flatMap(f -> f))
                .orElse(Stream.empty());
            List<CardGroup> groups = groupStreams
                .collect(Collectors.toList());
            outputGroups(groups, collector);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new IOException(e);
        } finally {
            fileLogger.close();
        }
    }

    private void outputGroups(List<CardGroup> groups, OutputLogCollector collector) throws IOException {
        AnkiFileWriter writer = new AnkiFileWriter(rootDir.toPath(), groups, true);
        writer.doWrite(collector);
    }

    private Stream<CardGroup> _getGroups(String uploadedFileName, AnkiFileLogger fileLogger, String question, boolean forPreview) throws IOException {
        File file = new File(rootDir, uploadedFileName);
        MarkdownImporter importer = addPreProcessors(new MarkdownImporter(file.getAbsolutePath(), fileLogger), question);
        List<CardGroup> groups = forPreview ? importer.doPreview() : importer.doImport();
        return groups.stream();
    }

    private MarkdownImporter addPreProcessors(MarkdownImporter importer, String question) {
        MetaInfoType meta = MetaInfoType.of(question).orElse(MetaInfoType.NONE);
        PreProcessor pp = new PreProcessor(meta);
        importer.addPreProcessor(pp::addMetaInfo);
        return importer;
    }

    public CardDTO sampleCard() {
        String question = "I can't *get _* (understand) how she manages to *get _ doing* so little work. | over, away with";
        String answer = "over, away with";
        String line = question + " | " + answer;

        TokenCard card = TokenCard.parse(line, question, answer);
        card.setTags("test-tag test-subtag");
        return new CardDTO(card);
    }
}
