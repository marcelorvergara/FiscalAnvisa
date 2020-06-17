package anvisa.inflabnet.fiscalizacao.ui.nuvem.anotacoes

import anvisa.inflabnet.fiscalizacao.cripto.CriptoString


//para envio ao Firebase
class AnotacaoDecipt(
    var idFiscal:String,
    var tituloAnotcao: String,
    var dataAnota: String,
    var pathTxt: String,
    var pathPhoto: CriptoString,
    var estabelecimento: String,
    var idAnotacao: Int
    )