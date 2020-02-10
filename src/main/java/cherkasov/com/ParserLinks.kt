package cherkasov.com

import java.io.FileNotFoundException
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Level

/**
 *
 * Parses the file with links and put them in the tasks queue
 */
class ParserLinks(private val fileName: String) {
    //if line is fewer than 12 chars length, then line is useless because there are not urls (http://a.b/c)
    private val urlLength = 12

    /**
     * Loads the file with links and splits it into the lines.
     * @return      a list of lines reading from the file
     * @throws      IOException if the file does not exist
     */
    @Throws(IOException::class)
    fun loadFile(): List<String> {
        if (!Files.exists(Paths.get(fileName))) {
            throw FileNotFoundException("File with links does not exist.")
        }
        return Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8)
    }

    /**
     * Adds tasks into a queue.
     * @param lines     the list with url and file name per line
     * @return          a task queue for downloading
     * @throws          NullPointerException if lines is null
     */
    fun parseLinks(lines: List<String>?): Queue<TaskEntity> {
        if (lines == null) {
            throw NullPointerException("List of links is null.")
        }
        val queueTasks: Queue<TaskEntity> = ConcurrentLinkedQueue()
        for (line in lines) {
            if (line.length < urlLength) {
                continue
            }
            //comment line, go to next line
            if (line[0] == '#') {
                continue
            }
            val urlAndFileName = line.trim { it <= ' ' }.split(" ").toTypedArray()
            //adds tasks to concurrency queue, from which the threads will be take url for download
            if (urlAndFileName.size >= 2) {
                queueTasks.add(TaskEntity(urlAndFileName[0], urlAndFileName[1]))
            }
        }
        ProjectLogger.LOG.log(Level.INFO, "Parsing file with links was done")
        return queueTasks
    }

}