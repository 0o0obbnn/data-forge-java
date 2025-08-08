package com.dataforge.generators.text;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Multilingual text generator for generating text in multiple languages.
 * Useful for testing internationalization and localization support.
 */
public class MultilingualTextGenerator implements DataGenerator<String> {
    
    // Supported languages
    public enum Language {
        CHINESE, ENGLISH, SPANISH, FRENCH, GERMAN, JAPANESE, KOREAN, ARABIC, RUSSIAN, MIXED
    }
    
    // Sample text fragments for different languages
    private static final List<String> CHINESE_FRAGMENTS = Arrays.asList(
        "这是一个示例文本片段，用于生成多语言文本数据。",
        "在国际化和本地化测试中，多语言文本是非常重要的资源。",
        "通过使用不同的语言，我们可以模拟全球用户的文本内容。",
        "多语言文本生成器可以根据需要生成指定语言的文本内容。",
        "文本可以包含各种标点符号和特殊字符，以增加真实性。",
        "在实际应用中，文本数据可能来自不同的语言和地区。",
        "为了提高生成效率，我们可以使用预定义的多语言文本片段库。",
        "多语言文本生成器应该支持多种语言和字符集。"
    );
    
    private static final List<String> ENGLISH_FRAGMENTS = Arrays.asList(
        "This is a sample text fragment for generating multilingual text data.",
        "In internationalization and localization testing, multilingual text is an important resource.",
        "By using different languages, we can simulate text content from global users.",
        "The multilingual text generator can generate text content in specified languages as needed.",
        "Text can contain various punctuation marks and special characters to increase realism.",
        "In real applications, text data may come from different languages and regions.",
        "To improve generation efficiency, we can use predefined multilingual text fragment libraries.",
        "Multilingual text generators should support multiple languages and character sets."
    );
    
    private static final List<String> SPANISH_FRAGMENTS = Arrays.asList(
        "Este es un fragmento de texto de ejemplo para generar datos de texto multilingüe.",
        "En las pruebas de internacionalización y localización, el texto multilingüe es un recurso importante.",
        "Al usar diferentes idiomas, podemos simular el contenido de texto de usuarios globales.",
        "El generador de texto multilingüe puede generar contenido de texto en idiomas especificados según sea necesario.",
        "El texto puede contener varios signos de puntuación y caracteres especiales para aumentar el realismo.",
        "En aplicaciones reales, los datos de texto pueden provenir de diferentes idiomas y regiones.",
        "Para mejorar la eficiencia de generación, podemos usar bibliotecas de fragmentos de texto multilingüe predefinidas.",
        "Los generadores de texto multilingüe deben admitir múltiples idiomas y conjuntos de caracteres."
    );
    
    private static final List<String> FRENCH_FRAGMENTS = Arrays.asList(
        "Ceci est un fragment de texte d'exemple pour générer des données textuelles multilingues.",
        "Dans les tests d'internationalisation et de localisation, le texte multilingue est une ressource importante.",
        "En utilisant différentes langues, nous pouvons simuler le contenu textuel des utilisateurs globaux.",
        "Le générateur de texte multilingue peut générer du contenu textuel dans les langues spécifiées selon les besoins.",
        "Le texte peut contenir divers signes de ponctuation et caractères spéciaux pour augmenter le réalisme.",
        "Dans les applications réelles, les données textuelles peuvent provenir de différentes langues et régions.",
        "Pour améliorer l'efficacité de génération, nous pouvons utiliser des bibliothèques de fragments de texte multilingues prédéfinies.",
        "Les générateurs de texte multilingue doivent prendre en charge plusieurs langues et jeux de caractères."
    );
    
    private static final List<String> GERMAN_FRAGMENTS = Arrays.asList(
        "Dies ist ein Beispiel-Textfragment zur Erstellung mehrsprachiger Textdaten.",
        "Bei Internationalisierungs- und Lokalisierungstests ist mehrsprachiger Text eine wichtige Ressource.",
        "Durch die Verwendung verschiedener Sprachen können wir Textinhalte globaler Benutzer simulieren.",
        "Der mehrsprachige Textgenerator kann je nach Bedarf Textinhalte in festgelegten Sprachen erzeugen.",
        "Text kann verschiedene Satzzeichen und Sonderzeichen enthalten, um die Realitätsnähe zu erhöhen.",
        "In echten Anwendungen können Textdaten aus verschiedenen Sprachen und Regionen stammen.",
        "Um die Generierungseffizienz zu verbessern, können wir vordefinierte Bibliotheken mit mehrsprachigen Textfragmenten verwenden.",
        "Mehrsprachige Textgeneratoren sollten mehrere Sprachen und Zeichensätze unterstützen."
    );
    
    private static final List<String> JAPANESE_FRAGMENTS = Arrays.asList(
        "これは、多言語テキストデータを生成するためのサンプルテキストフラグメントです。",
        "国際化とローカライズのテストでは、多言語テキストが重要なリソースです。",
        "さまざまな言語を使用することで、グローバルユーザーのテキストコンテンツをシミュレートできます。",
        "多言語テキストジェネレータは、必要に応じて指定された言語でテキストコンテンツを生成できます。",
        "テキストには、現実感を高めるために、さまざまな句読点や特殊文字を含めることができます。",
        "実際のアプリケーションでは、テキストデータは異なる言語や地域から来る可能性があります。",
        "生成効率を向上させるために、事前に定義された多言語テキストフラグメントライブラリを使用できます。",
        "多言語テキストジェネレータは、複数の言語と文字セットをサポートする必要があります。"
    );
    
    private static final List<String> KOREAN_FRAGMENTS = Arrays.asList(
        "이것은 다국어 텍스트 데이터를 생성하기 위한 샘플 텍스트 조각입니다.",
        "국제화 및 현지화 테스트에서 다국어 텍스트는 중요한 리소스입니다.",
        "다양한 언어를 사용하여 글로벌 사용자의 텍스트 콘텐츠를 시뮬레이션할 수 있습니다.",
        "다국어 텍스트 생성기는 필요에 따라 지정된 언어로 텍스트 콘텐츠를 생성할 수 있습니다.",
        "텍스트에는 현실감을 높이기 위해 다양한 구두점과 특수 문자가 포함될 수 있습니다.",
        "실제 애플리케이션에서 텍스트 데이터는 다양한 언어와 지역에서 올 수 있습니다.",
        "생성 효율성을 높이기 위해 미리 정의된 다국어 텍스트 조각 라이브러리를 사용할 수 있습니다.",
        "다국어 텍스트 생성기는 여러 언어와 문자 집합을 지원해야 합니다."
    );
    
    private static final List<String> ARABIC_FRAGMENTS = Arrays.asList(
        "هذا مقطع نصي عينة لتوليد بيانات النصوص متعددة اللغات.",
        "في اختبارات التدويل والتوطين، يُعتبر النص متعدد اللغات موردًا مهمًا.",
        "باستخدام لغات مختلفة، يمكننا محاكاة محتوى النص للمستخدمين العالميين.",
        "يمكن لمولد النصوص متعدد اللغات إنشاء محتوى نصي باللغات المحددة حسب الحاجة.",
        "يمكن أن يحتوي النص على علامات ترقيم وحروف خاصة متنوعة لزيادة الواقعية.",
        "في التطبيقات الحقيقية، قد تأتي بيانات النص من لغات ومناطق مختلفة.",
        "لتحسين كفاءة الإنشاء، يمكننا استخدام مكتبات مقاطع النصوص متعددة اللغات المعرفة مسبقًا.",
        "يجب أن تدعم أجهزة توليد النصوص متعددة اللغات عدة لغات ومجموعات أحرف."
    );
    
    private static final List<String> RUSSIAN_FRAGMENTS = Arrays.asList(
        "Это пример текстового фрагмента для генерации многоязычных текстовых данных.",
        "В тестировании интернационализации и локализации многоязычный текст является важным ресурсом.",
        "Используя разные языки, мы можем имитировать текстовый контент глобальных пользователей.",
        "Генератор многоязычного текста может генерировать текстовый контент на указанных языках по мере необходимости.",
        "Текст может содержать различные знаки препинания и специальные символы для повышения реализма.",
        "В реальных приложениях текстовые данные могут поступать из разных языков и регионов.",
        "Для повышения эффективности генерации мы можем использовать предопределенные библиотеки фрагментов многоязычного текста.",
        "Генераторы многоязычного текста должны поддерживать несколько языков и наборов символов."
    );
    
    private final Language language;
    private final int minLength;
    private final int maxLength;
    private final boolean mixLanguages;
    
    /**
     * Creates a multilingual text generator with default settings.
     */
    public MultilingualTextGenerator() {
        this(Language.MIXED, 50, 200, true);
    }
    
    /**
     * Creates a multilingual text generator.
     * 
     * @param language the language to generate text in
     * @param minLength minimum length of the generated text
     * @param maxLength maximum length of the generated text
     * @param mixLanguages whether to mix multiple languages
     */
    public MultilingualTextGenerator(Language language, int minLength, int maxLength, boolean mixLanguages) {
        // Validate parameters
        if (minLength <= 0) {
            throw new IllegalArgumentException("minLength must be positive");
        }
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength must be >= minLength");
        }
        
        this.language = language;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.mixLanguages = mixLanguages;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Determine the target length
        int targetLength = minLength + random.nextInt(maxLength - minLength + 1);
        
        StringBuilder text = new StringBuilder();
        
        // Select the language fragments to use
        List<String> fragments = getLanguageFragments(language, random);
        
        // Generate text by randomly selecting and combining fragments
        while (text.length() < targetLength) {
            // Add a fragment
            String fragment = fragments.get(random.nextInt(fragments.size()));
            
            // If mixing languages, occasionally switch to another language
            if (mixLanguages && random.nextInt(5) == 0) { // 20% chance
                Language newLanguage = Language.values()[random.nextInt(Language.values().length - 1)]; // Exclude MIXED
                fragments = getLanguageFragments(newLanguage, random);
            }
            
            // If adding this fragment would exceed the target length significantly, 
            // trim it to fit better
            if (text.length() + fragment.length() > targetLength + 50) {
                int charsNeeded = targetLength - text.length();
                if (charsNeeded > 10) { // Only trim if we need more than 10 chars
                    fragment = fragment.substring(0, Math.min(fragment.length(), charsNeeded));
                } else {
                    // If we're close to target, break the loop
                    break;
                }
            }
            
            text.append(fragment);
            
            // Randomly add spaces
            if (random.nextInt(3) == 0) { // 33% chance
                text.append(" ");
            }
            
            // Occasionally add a line break for paragraph structure
            if (random.nextInt(10) == 0 && text.length() > 50) { // 10% chance after 50 chars
                text.append("\n");
            }
        }
        
        // Ensure minimum length is met
        while (text.length() < minLength) {
            String fragment = fragments.get(random.nextInt(fragments.size()));
            text.append(fragment);
        }
        
        // Make sure we have enough content
        if (text.length() < targetLength) {
            // Add more fragments until we reach target length
            while (text.length() < targetLength) {
                String fragment = fragments.get(random.nextInt(fragments.size()));
                if (text.length() + fragment.length() <= targetLength) {
                    text.append(fragment);
                } else {
                    // Add a substring of the fragment to reach exactly target length
                    int needed = targetLength - text.length();
                    if (needed > 0) {
                        text.append(fragment.substring(0, Math.min(needed, fragment.length())));
                    }
                    break;
                }
            }
        }
        
        // Trim to exact target length if necessary
        if (text.length() > targetLength) {
            // Try to trim at a word boundary
            String result = text.substring(0, targetLength);
            int lastSpace = result.lastIndexOf(' ');
            if (lastSpace > targetLength - 20 && lastSpace > 0) { // If close to the end
                result = result.substring(0, lastSpace);
            }
            return result;
        }
        
        return text.toString();
    }
    
    /**
     * Gets the language fragments for the specified language.
     * 
     * @param language the language
     * @param random the random number generator
     * @return the language fragments
     */
    private List<String> getLanguageFragments(Language language, Random random) {
        switch (language) {
            case CHINESE:
                return CHINESE_FRAGMENTS;
            case ENGLISH:
                return ENGLISH_FRAGMENTS;
            case SPANISH:
                return SPANISH_FRAGMENTS;
            case FRENCH:
                return FRENCH_FRAGMENTS;
            case GERMAN:
                return GERMAN_FRAGMENTS;
            case JAPANESE:
                return JAPANESE_FRAGMENTS;
            case KOREAN:
                return KOREAN_FRAGMENTS;
            case ARABIC:
                return ARABIC_FRAGMENTS;
            case RUSSIAN:
                return RUSSIAN_FRAGMENTS;
            case MIXED:
            default:
                // Randomly select a language
                Language[] languages = {Language.CHINESE, Language.ENGLISH, Language.SPANISH, 
                                      Language.FRENCH, Language.GERMAN, Language.JAPANESE, 
                                      Language.KOREAN, Language.ARABIC, Language.RUSSIAN};
                Language randomLanguage = languages[random.nextInt(languages.length)];
                return getLanguageFragments(randomLanguage, random);
        }
    }
    
    @Override
    public String getName() {
        return "multilingual_text";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("language", "min_length", "max_length", "mix_languages");
    }
}