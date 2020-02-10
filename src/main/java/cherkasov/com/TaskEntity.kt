package cherkasov.com

/**
 * The entity that stores address for downloading files and file name
 * to store on filesystem
 * It is used to represent tasks in the task queue
 */
class TaskEntity(val url: String, val fileName: String) {

    /**
     * Uses for unit testing
     */
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as TaskEntity
        return if (url != that.url) false else fileName == that.fileName
    }

}