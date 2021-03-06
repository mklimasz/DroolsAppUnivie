package utils;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.io.ResourceFactory;

public class DroolsSessionUtils {

    private DroolsSessionUtils() {}

    public static KieSession createKieSession(String... drlResourcesPaths) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kcontainer = createKieContainer(ks, drlResourcesPaths);
        KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
        KieBase kbase = kcontainer.newKieBase(kbconf);
        KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
        return kbase.newKieSession(ksconf, null);
    }

    private static KieContainer createKieContainer(KieServices ks, String... drlResourcesPaths) {
        KieFileSystem kfs = ks.newKieFileSystem();
        for (String path : drlResourcesPaths) {
            kfs.write(ResourceFactory.newClassPathResource(path));
        }
        KieBuilder kbuilder = ks.newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new IllegalArgumentException(kbuilder.getResults().toString());
        }
        ReleaseId relId = kbuilder.getKieModule().getReleaseId();
        return ks.newKieContainer(relId);
    }
}
