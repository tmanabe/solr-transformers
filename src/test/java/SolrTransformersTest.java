import io.github.tmanabe.demo2.Demo2Params;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.util.RestTestBase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Base64;

public class SolrTransformersTest extends RestTestBase {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        File testHome = createTempDir().toFile();
        FileUtils.copyDirectory(getFile("solr"), testHome);
        File testLog = createTempDir().toFile();
        System.setProperty("solr.log.dir", testLog.getAbsolutePath());

        createJettyAndHarness(testHome.getAbsolutePath(), "solrconfig.xml", "schema.xml", "/solr", true, null);
    }

    @Before
    public void setUpBefore() {
        assertU(adoc("id", "0", "vector", "LINE株式会社で[MASK]の研究・開発をしている。"));
        assertU(commit());
    }

    private static String base64StringOfConstantVector(int length) {
        byte[] bytes = new byte[Float.BYTES * length];
        FloatBuffer floatBuffer = ByteBuffer.wrap(bytes).asFloatBuffer();
        for (int i = 0; i < length; ++i) {
            floatBuffer.put(1f);
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Test
    public void testBasic() throws Exception {
        SolrQuery query = new SolrQuery();
        query.add(CommonParams.Q, "*:*");
        query.add(CommonParams.FL, "score");
        query.add(Demo2Params.DEMO2_FIELD_NAME, "vector");
        query.set(Demo2Params.DEMO2_QUERY_VECTOR, base64StringOfConstantVector(768));
        assertJQ("/select" + query.toQueryString(),
                "/response/numFound==1", "/response/docs/[0]/score==1.9815359");
    }
}
