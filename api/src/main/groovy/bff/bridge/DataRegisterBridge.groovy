package bff.bridge

interface DataRegisterBridge {

    Boolean sendMessage(String googleSpreadsheetId, List<String> values)

    String getSpreadsheetIdSupplierLead()
}