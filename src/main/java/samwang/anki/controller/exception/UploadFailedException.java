package samwang.anki.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Failed to upload")
public class UploadFailedException extends RuntimeException {
    public UploadFailedException(Exception rootCause) {
        super(rootCause);
    }
}
