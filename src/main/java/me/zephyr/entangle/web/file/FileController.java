package me.zephyr.entangle.web.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;

@Controller
@RequestMapping("/file")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @RequestMapping("/download/{filename}")
    @ResponseBody
    public String downloadFile(@PathVariable("filename") String fileName) {
        return "ok:" + fileName;
    }

    @RequestMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(InputStream inputStream) {
    }
}
