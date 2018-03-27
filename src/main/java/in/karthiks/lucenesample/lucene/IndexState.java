package in.karthiks.lucenesample.lucene;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;

@Component
public class IndexState {

    @Autowired
    private LuceneConfig config;
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    public IndexState(LuceneConfig config) {
        this.config = config;
        openIndex();
    }

    public void indexUpdated() {
        try {
            refreshSearchContext();
        } catch (IOException e) {
            System.err.println("Error refreshingSearchContext. " + e.getMessage());
        }
    }

    private void refreshSearchContext() throws IOException {
        if(indexReader != null)
            indexReader.close();
        openIndex();
    }

    private boolean openIndex() {
        try {
            Directory dir = FSDirectory.open(Paths.get(config.getIndexLocation()));
            indexReader = DirectoryReader.open(dir);
            indexSearcher = new IndexSearcher(indexReader);
            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());
        }
        return false;
    }

    public IndexSearcher getIndexSearcher() {
        return indexSearcher;
    }
}
