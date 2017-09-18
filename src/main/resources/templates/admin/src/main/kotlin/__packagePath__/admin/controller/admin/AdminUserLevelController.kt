package @packageName@.admin.controller.admin

//import com.mobisist.outsourcing.springboothomerseed.infra.db.tables.pojos.QuizUserLevel
//import com.mobisist.outsourcing.springboothomerseed.infra.service.core.QuizUserLevelService
//import com.mobisist.outsourcing.springboothomerseed.infra.service.dto.QuizUserLevelInput
//import com.mobisist.swordess.springbootseed.infra.web.ApiResult
//import com.mobisist.swordess.springbootseed.infra.web.plugin.DataTablePage
//import com.mobisist.swordess.springbootseed.infra.web.plugin.DataTablePageable
//import com.mobisist.swordess.springbootseed.infra.web.plugin.find
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import javax.validation.Valid
//
//@RestController
//@RequestMapping("/admin/userLevels")
//open class AdminUserLevelController {
//
//    private val logger = LoggerFactory.getLogger(AdminUserLevelController::class.java)
//
//    @Autowired
//    private lateinit var quizUserLevelService: QuizUserLevelService
//
//    @PostMapping
//    open fun createOrUpdate(@Valid input: QuizUserLevelInput): ResponseEntity<ApiResult<*>> {
//        logger.info("createOrUpdate - input: $input")
//        val record = input.id?.let { quizUserLevelService.findOne(it)!! } ?: QuizUserLevel()
//        record.apply {
//            name = input.name
//            minPoints = input.minPoints
//            maxPoints = input.maxPoints
//            pointToRmbRatio = input.pointToRmbRatio
//        }
//
//        if (record.id == null) {
//            quizUserLevelService.save(record)
//        } else {
//            quizUserLevelService.update(record)
//        }
//
//        return ApiResult.ok()
//    }
//
//    @GetMapping
//    open fun listPage(dataTablePageable: DataTablePageable): DataTablePage<QuizUserLevel> {
//        return dataTablePageable.find { quizUserLevelService.findAll(it) }
//    }
//
//    @DeleteMapping("/{id}")
//    open fun delete(@PathVariable id: Long): ResponseEntity<ApiResult<*>> {
//        quizUserLevelService.delete(id)
//        return ApiResult.ok()
//    }
//
//}