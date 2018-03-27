package in.karthiks.lucenesample.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.file.Paths;
@Deprecated
public class LuceneIndexWriter {

    static final String INDEX_PATH = "indexDir";
    static final String JSON_FILE_PATH = "src/main/resources/sample.json";
    static final String STOPWORDS_FILE_PATH = "src/main/resources/stopwords.txt";

    String indexPath;
    String jsonFilePath;
    IndexWriter indexWriter = null;
    IndexReader indexReader = null;
    IndexSearcher indexSearcher;
    private Analyzer analyzer;

    public LuceneIndexWriter(String indexPath, String jsonFilePath) {
        this.indexPath = indexPath;
        this.jsonFilePath = jsonFilePath;
    }

    public void createIndex() throws FileNotFoundException {
        JSONArray jsonObjects = parseJSONFile();
        openIndex(true);
        addDocuments(jsonObjects);
        finish();
    }

    public JSONArray parseJSONFile() throws FileNotFoundException {
        InputStream jsonFile = new FileInputStream(jsonFilePath);
        Reader readerJson = new InputStreamReader(jsonFile);
        Object fileObjects = new JSONTokener(readerJson).nextValue();
        JSONArray arrayObjects = (JSONArray) fileObjects;
        return arrayObjects;
    }

    public boolean openIndex(boolean forWrite) {
        try {
            InputStream stopWords = new FileInputStream(STOPWORDS_FILE_PATH);
            Reader readerStopWords = new InputStreamReader(stopWords);
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            analyzer = new StandardAnalyzer(readerStopWords);
            if(forWrite) {
                IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                indexWriter = new IndexWriter(dir, iwc);
            }
            indexReader = DirectoryReader.open(dir);
            indexSearcher = new IndexSearcher(indexReader);
            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());
        }
        return false;
    }

    /**
     * Add documents to the index
     */
    public void addDocuments(JSONArray jsonObjects) {
        for (Object o : jsonObjects) {
            JSONObject object = (JSONObject)o;
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
    }

    public void finish() {
        try {
            indexWriter.commit();
            indexWriter.close();
            indexReader.close();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }

    public void query(String text) throws IOException, ParseException {
        Query query = new QueryParser("divNum", analyzer).parse("14");
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("Total Hits : " + topDocs.totalHits);
        ScoreDoc[] docs = topDocs.scoreDocs;
        for(ScoreDoc d : docs) {
            System.out.println(indexSearcher.doc(d.doc).get("__originalContent__"));
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        LuceneIndexWriter liw = new LuceneIndexWriter(INDEX_PATH, JSON_FILE_PATH);
        liw.createIndex();
        liw.openIndex(false);
        liw.query("");
    }



}