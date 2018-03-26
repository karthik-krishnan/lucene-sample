package in.karthiks.lucenesample.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "lucene")
@Configuration
public class LuceneConfig {
    private String indexLocation;
    private String inputFile;
    private String stopwordsLocation;

    public String getIndexLocation() {
        return indexLocation;
    }

    public void setIndexLocation(String indexLocation) {
        this.indexLocation = indexLocation;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getStopwordsLocation() {
        return stopwordsLocation;
    }

    public void setStopwordsLocation(String stopwordsLocation) {
        this.stopwordsLocation = stopwordsLocation;
    }

    public Analyzer getAnalyzer() throws IOException {
        InputStream stopWords = this.getClass().getClassLoader().getResourceAsStream(getStopwordsLocation());
        Reader readerStopWords = new InputStreamReader(stopWords);
        return new StandardAnalyzer(readerStopWords);
    }
}