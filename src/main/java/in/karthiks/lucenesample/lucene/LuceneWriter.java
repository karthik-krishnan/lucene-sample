package in.karthiks.lucenesample.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@Component
public class LuceneWriter {
    private IndexWriter indexWriter;
    private LuceneConfig config;
    private IndexState indexState;

    @Autowired
    private LuceneWriter(LuceneConfig config, IndexState indexState) {
        this.config = config;
        this.indexState = indexState;
    }

    private long indexContents(InputStream inputStream) throws FileNotFoundException {
        JSONArray jsonObjects = parseJSONFromStream(inputStream);
        openIndex();
        long count = addDocuments(jsonObjects);
        finish();
        return count;
    }

    private long addDocuments(JSONArray jsonObjects) {
        for (Object o : jsonObjects) {
            org.json.JSONObject object = (org.json.JSONObject)o;
            Document doc = new Document();
            final FieldType bodyOptions = new FieldType();
            bodyOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            bodyOptions.setStored(true);
            bodyOptions.setStoreTermVectors(true);
            bodyOptions.setTokenized(true);
            for (String field : object.keySet()) {
                doc.add(new Field(field, String.valueOf(object.get(field)), bodyOptions));
            }
            final FieldType originalContentOptions = new FieldType();
            originalContentOptions.setIndexOptions(IndexOptions.NONE);
            originalContentOptions.setStored(true);
            originalContentOptions.setStoreTermVectors(false);
            originalContentOptions.setTokenized(false);
            doc.add(new Field("__originalContent__", object.toString(), originalContentOptions));

            try {
                System.out.println(doc);
                indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " + ex.getMessage());
            }
        }
        return jsonObjects.length();
    }

    private void finish() {
        try {
            indexWriter.commit();
            indexWriter.close();
            indexState.indexUpdated();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }

    private JSONArray parseJSONFromStream(InputStream inputStream) throws FileNotFoundException {
        Object fileObjects = new JSONTokener(inputStream).nextValue();
        JSONArray arrayObjects = (JSONArray) fileObjects;
        return arrayObjects;
    }

    private boolean openIndex() {
        try {
            Directory dir = FSDirectory.open(Paths.get(config.getIndexLocation()));
            IndexWriterConfig iwc = new IndexWriterConfig(config.getAnalyzer());
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(dir, iwc);
            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());
        }
        return false;
    }

    public long store(InputStream inputStream) throws FileNotFoundException {
        return indexContents(inputStream);
    }
}
