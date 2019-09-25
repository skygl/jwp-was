package webserver;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import java.io.IOException;
import java.util.Map;

public class TemplatesFileLoader {
    private static final String PREFIX = "/templates";
    private static final String SUFFIX = "";

    public static String load(String viewName, Map<String, Object> model) throws IOException {
        TemplateLoader templateLoader = new ClassPathTemplateLoader();
        templateLoader.setPrefix(PREFIX);
        templateLoader.setSuffix(SUFFIX);
        Handlebars handlebars = new Handlebars(templateLoader);
        handlebars.registerHelper("inc", (Helper<Integer>) (leftOperand, options) -> leftOperand + 1);

        Template template = handlebars.compile(viewName);

        return template.apply(model);
    }
}
