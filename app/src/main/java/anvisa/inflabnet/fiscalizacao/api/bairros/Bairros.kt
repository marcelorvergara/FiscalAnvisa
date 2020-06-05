package anvisa.inflabnet.fiscalizacao.api.bairros

class Bairros (
    var features: List<Atributes>
) {
    class Atributes (
        var attributes: Nome
    ) {
        class Nome (
            var NOME: String
        )
    }
}