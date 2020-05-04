package ru.timelimit.relationship

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.timelimit.account.Users
import ru.timelimit.utility.AccountUtility

@RestController
class DoctorController {
    data class PatientResult(
            val patientId: Int,
            val patientName: String
    )

    data class ListPatientsResult(
            val status: Boolean,
            val patients: List<PatientResult> = listOf()
    )

    @RequestMapping("doctor/listPatients")
    fun listPatients(@RequestParam("token") token: String) : ListPatientsResult {
        val doctorProfile = AccountUtility.getUserByToken(token) ?: return ListPatientsResult(false)
        if (doctorProfile[Users.role] != 1) return ListPatientsResult(false)

        val listPatients = mutableListOf<PatientResult>()
        transaction {
            val result = Relationship.select { Relationship.doctor_id eq doctorProfile[Users.id] and Relationship.status }
            result.forEach {
                val patientProfile = AccountUtility.getUserById(it[Relationship.patient_id])
                if (patientProfile != null) {
                    listPatients.add(PatientResult(it[Relationship.patient_id],
                            patientProfile[Users.firstName] + " " + patientProfile[Users.lastName]))
                }
            }
        }

        return ListPatientsResult(true, listPatients)
    }

    @RequestMapping("doctor/listSuggestions")
    fun listSuggestions(@RequestParam("token") token: String) : ListPatientsResult {
        val doctorProfile = AccountUtility.getUserByToken(token) ?: return ListPatientsResult(false)
        if (doctorProfile[Users.role] != 1) return ListPatientsResult(false)

        val listPatients = mutableListOf<PatientResult>()
        transaction {
            val result = Relationship.select { Relationship.doctor_id eq doctorProfile[Users.id] and not(Relationship.status) }
            result.forEach {
                val patientProfile = AccountUtility.getUserById(it[Relationship.patient_id])
                if (patientProfile != null) {
                    listPatients.add(PatientResult(it[Relationship.patient_id],
                            patientProfile[Users.firstName] + " " + patientProfile[Users.lastName]))
                }
            }
        }

        return ListPatientsResult(true, listPatients)
    }

    @RequestMapping("doctor/applySuggestion")
    fun applySuggestion(@RequestParam("patientId") patientId: Int, @RequestParam("token") token: String) : Map<String, Boolean> {
        val doctorProfile = AccountUtility.getUserByToken(token) ?: return mapOf(Pair("status", false))
        if (doctorProfile[Users.role] != 1) return mapOf(Pair("status", false))

        var status = false
        transaction {
            val result = Relationship.select {
                (Relationship.doctor_id eq doctorProfile[Users.id]) and (Relationship.patient_id eq patientId) and
                        not(Relationship.status) }
            if (!result.empty()) {
                Relationship.update( {
                    (Relationship.doctor_id eq doctorProfile[Users.id]) and (Relationship.patient_id eq patientId) and
                            not(Relationship.status) }) {
                    it[Relationship.status] = true
                }
                status = true
            }
        }

        return mapOf(Pair("status", status))
    }
}
