package samwang.anki.controller;

import com.samwang.anki.impl.model.OutputLogCollector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import samwang.anki.controller.exception.FileExistingException;
import samwang.anki.controller.exception.UploadFailedException;
import samwang.anki.model.CardDTO;
import samwang.anki.model.CardGroupDTO;
import samwang.anki.model.OutputDTO;
import samwang.anki.model.OutputLogCollectorDTO;
import samwang.anki.service.PreviewService;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
public class AnkiImporterPreviewController {

    private static final String ROOT_DIR = "/Users/samwang/anki.temp";
    private PreviewService previewService;

    // TODO inject with spring?
    public AnkiImporterPreviewController() {
        previewService = new PreviewService(ROOT_DIR);
    }

    @GetMapping("/echo")
    public String echo(@RequestParam(value = "message", defaultValue = "[Default echo message]") String msg) {
        return String.format("Echo from server: %s at %s", msg, new Date());
    }

    @GetMapping("/echoCard")
    public CardDTO echoCard() {
        return previewService.sampleCard();
    }

    @GetMapping("/files")
    public List<String> getFiles() {
        return previewService.getFiles();
    }

    @GetMapping("/clear")
    public int clear() {
        return previewService.clearFiles();
    }

    @GetMapping("/groups")
    public List<CardGroupDTO> getGroups(
        @RequestParam(value = "fileName") String fileName,
        @RequestParam(value = "question", defaultValue = "") String question
    ) {
        try {
            return previewService.getGroups(fileName, question);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    @GetMapping("/output")
    public OutputLogCollectorDTO output(
        @RequestParam(value = "fileName") String fileName,
        @RequestParam(value = "question", defaultValue = "") String question
    ) {

        OutputLogCollector collector = new OutputLogCollector();
        try {
            previewService.outputGroups(fileName, collector, question);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            return new OutputLogCollectorDTO(collector);
        }
    }

    @PostMapping("/outputs")
    public OutputLogCollectorDTO outputs(@RequestBody OutputDTO outputCommand) {

        OutputLogCollector collector = new OutputLogCollector();
        try {
            previewService.outputGroups(outputCommand, collector);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            return new OutputLogCollectorDTO(collector);
        }
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            File dest = previewService.beforeSave(new File(ROOT_DIR, file.getOriginalFilename()));
            if (dest == null) throw new FileExistingException(file.getOriginalFilename());

            file.transferTo(dest);
            return dest.getName();

        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new UploadFailedException(e);
        }
    }
}
