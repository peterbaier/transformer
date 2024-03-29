package tech.baier.transformer.services


import spock.lang.Specification
import spock.lang.Unroll

class TransformerServiceTest extends Specification {

    TransformerService testObject

    def cleanup() {
        new File("csvTestRawInput.json").delete()
    }

    @Unroll
    def "Process file and transform"() {
        given:
        testObject = new TransformerService()
        def root = "${new File("").getAbsolutePath()}/src/test/resources"

        when:
        testObject.process("${root}/${inputFile}")

        then:
        def result = new File("${expectedResultFile}")
        assert result.exists()
//        String expectedJson = new String(Files.readAllBytes(Paths.get("${root}/${expectedFileContent}")))
//        String actualJson = new String(Files.readAllBytes(Paths.get(result.getAbsolutePath())))
//        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);

        where:
        inputFile             | expectedResultFile     | expectedFileContent
        "csvTestRawInput.csv" | "csvTestRawInput.json" | "expectedSuccess.json"
    }
}
