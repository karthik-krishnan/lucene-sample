package in.karthiks.lucenesample.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class LuceneReader {
    private LuceneConfig config;
    private IndexState indexState;

    @Autowired
    public LuceneReader(LuceneConfig config, IndexState indexState) {
        this.config = config;
        this.indexState = indexState;
    }

    public List query(String text) throws IOException, ParseException, org.json.simple.parser.ParseException {
        Query query = new QueryParser("", config.getAnalyzer()).parse(text);
        TopDocs topDocs = indexState.getIndexSearcher().search(query, 10);
        System.out.println("Total Hits : " + topDocs.totalHits);
        ScoreDoc[] docs = topDocs.scoreDocs;
        List contentArray = new ArrayList();
        JSONParser parser = new JSONParser();
        for(ScoreDoc d : docs) {
            String content = indexState.getIndexSearcher().doc(d.doc).get("__originalContent__");
            JSONObject json = (JSONObject) parser.parse(content);
            contentArray.add(json);
        }
        return contentArray;
    }
}
