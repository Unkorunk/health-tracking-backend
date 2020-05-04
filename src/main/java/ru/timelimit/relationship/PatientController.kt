package ru.timelimit.relationship

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.timelimit.account.Users
import ru.timelimit.utility.AccountUtility

@RestController
class PatientController {
    data class DoctorResult(
            val doctorId: Int,
            val doctorName: String
    )

    data class ListDoctorsResult(
            val status: Boolean,
            val patients: List<DoctorResult> = listOf()
    )

    @RequestMapping("patient/listMyDoctors")
    fun listMyDoctors(@RequestParam("token") token: String) : ListDoctorsResult {
        val patientProfile = AccountUtility.getUserByToken(token) ?: return ListDoctorsResult(false)
        if (patientProfile[Users.role]) return ListDoctorsResult(false)

        val listDoctors = mutableListOf<DoctorResult>()
        transaction {
            val result = Relationship.select { Relationship.patient_id eq patientProfile[Users.id] and Relationship.status }
            result.forEach {
                val doctorProfile = AccountUtility.getUserById(it[Relationship.doctor_id])
                if (doctorProfile != null) {
                    listDoctors.add(DoctorResult(it[Relationship.doctor_id],
                            doctorProfile[Users.firstName] + " " + doctorProfile[Users.lastName]))
                }
            }
        }

        return ListDoctorsResult(true, listDoctors)
    }

    @RequestMapping("patient/listAllDoctors")
    fun listAllDoctors(@RequestParam("token") token: String) : ListDoctorsResult {
        val patientProfile = AccountUtility.getUserByToken(token) ?: return ListDoctorsResult(false)
        if (patientProfile[Users.role]) return ListDoctorsResult(false)

        val listDoctors = mutableListOf<DoctorResult>()
        transaction {
            val result = Users.select { Users.role eq(true) }
            result.forEach {
                listDoctors.add(DoctorResult(it[Users.id],it[Users.firstName] + " " + it[Users.lastName]))
            }
        }

        return ListDoctorsResult(true, listDoctors)
    }

    @RequestMapping("patient/sendSuggestion")
    fun sendSuggestion(@RequestParam("doctorId") doctorId: Int, @RequestParam("token") token: String) : Map<String, Boolean> {
        val patientProfile = AccountUtility.getUserByToken(token) ?: return mapOf(Pair("status", false))
        if (patientProfile[Users.role]) return mapOf(Pair("status", false))

        var status = false
        transaction {
            val result = Relationship.select { (Relationship.doctor_id eq doctorId) and (Relationship.patient_id eq patientProfile[Users.id]) }
            if (result.empty()) {
                Relationship.insert {
                    it[Relationship.doctor_id] = doctorId
                    it[Relationship.patient_id] = patientProfile[Users.id]
                    it[Relationship.status] = false
                }
                status = true
            }
        }

        return mapOf(Pair("status", status))
    }
}