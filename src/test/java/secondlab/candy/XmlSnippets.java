package secondlab.candy;

import secondlab.candy.xml.XmlConstants;

public final class XmlSnippets {
    private XmlSnippets() {
    }

    public static String document(String body) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <Candy xmlns="%s">
                %s
                </Candy>
                """.formatted(XmlConstants.NAMESPACE, body);
    }

    public static String candy(String attributes, String energy, String kind, String ingredientsTail) {
        return """
                <candy %s>
                    <name>Sample</name>
                    <energy>%s</energy>
                    <type filling="false">
                        <kind>%s</kind>
                    </type>
                    <ingredients>
                        <water>1</water>
                        <sugar>2</sugar>
                        <fructose>3</fructose>
                        %s
                        <vanillin>4</vanillin>
                    </ingredients>
                    <value>
                        <proteins>1.00</proteins>
                        <fats>2.00</fats>
                        <carbohydrates>3.00</carbohydrates>
                    </value>
                    <production>Plant</production>
                </candy>
                """.formatted(attributes, energy, kind, ingredientsTail);
    }
}
