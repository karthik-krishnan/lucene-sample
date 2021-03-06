package in.karthiks.lucenesample.controller;

import in.karthiks.lucenesample.lucene.LuceneReader;
import in.karthiks.lucenesample.lucene.LuceneWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RestController
@Validated
public class LuceneController {
    @Autowired
    private LuceneReader reader;

    @Autowired
    private LuceneWriter writer;

    @PostMapping("/load")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        long count = writer.store(file.getInputStream());
        return "Loaded " + count + " documents.";
    }

    @GetMapping("/search")
    public ResponseEntity index(@RequestParam("search") String queryString) throws IOException, ParseException, org.json.simple.parser.ParseException {
        List list = reader.query(queryString);
        return ResponseEntity.ok(list);
    }

}

