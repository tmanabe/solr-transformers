import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.util.RestTestBase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

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

    @Test
    public void testBasic() throws Exception {
        SolrQuery query = new SolrQuery();
        query.add(CommonParams.Q, "id:0");
        query.add(CommonParams.FL, "vector");
        assertJQ("/select" + query.toQueryString(),
                "/response/numFound==1", "/response/docs/[0]/vector=='vIW/3Q=='");
    }
}
