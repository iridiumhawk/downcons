package cherkasov.com

/**
 * Stores the command line parameters
 */
class Parameters(numberOfThreads: Int, maxDownloadSpeed: Long, fileNameWithLinks: String?, outputFolder: String?, debug: Boolean) {
    val numberOfThreads: Int = if (numberOfThreads > 0) numberOfThreads else 1
    //bytes in second
    val maxDownloadSpeed: Long = if (maxDownloadSpeed > 0) maxDownloadSpeed else 1000000
    val fileNameWithLinks: String = if (fileNameWithLinks == null || "" == fileNameWithLinks) "links.txt" else fileNameWithLinks
    val outputFolder: String = if (outputFolder == null || "" == outputFolder) "download" else outputFolder
    var isDebug = debug

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Parameters
        if (numberOfThreads != that.numberOfThreads) return false
        if (maxDownloadSpeed != that.maxDownloadSpeed) return false
        if (isDebug != that.isDebug) return false
        return if (fileNameWithLinks != that.fileNameWithLinks) false else outputFolder == that.outputFolder
    }

    override fun hashCode(): Int {
        var result = numberOfThreads
        result = 31 * result + java.lang.Long.hashCode(maxDownloadSpeed)
        result = 31 * result + fileNameWithLinks.hashCode()
        result = 31 * result + outputFolder.hashCode()
        result = 31 * result + if (isDebug) 1 else 0
        return result
    }

    companion object {
        @JvmField
        var EMPTY = Parameters(0, 0, "", "", false)
    }
}