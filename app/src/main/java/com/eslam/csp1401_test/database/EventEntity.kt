package com.eslam.csp1401_test.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eslam.csp1401_test.AttendeesItem
import com.eslam.csp1401_test.EventItem
import com.eslam.csp1401_test.Location
import com.eslam.csp1401_test.Status


@Entity(tableName = "EventEntities")
data class EventEntity(

    @PrimaryKey
    val id: String,

    val subject: String? = null,


    val entityAttendees: List<Attendee>? = null,

    val removedReason:String? =null,


    val organizerAddress: String? = null,
    val organizerName: String? = null,


    val odataEtag: String? = null,



    val startDateTime: String? = null,

    val startTimeZone: String? = null,


    val bodyPreview: String? = null,



    val entityResponse: String? = null,

    val entityResponseTime: String? = null,


    val endDateTime: String? = null,

    val endTimeZone: String? = null,


    val locationUniqueIdType: String? = null,

    val locationDisplayName: String? = null,

    val locationType: String? = null,

    val locationUniqueId: String? = null,


    val bodyContentType: String? = null,
    val bodyContent: String? = null,


    val isCancelled: Boolean? = null


)

data class Attendee(

    val attendeesAddress: String? = null,
    val attendeesName: String? = null,
    val attendeesType: String? = null,
    val attendeesStatusResponse: String? = null,
    val attendeesStatusTime: String? = null
)
//
//
//data class EntityLocation(
//
//
//    val uniqueIdType: String? = null,
//
//
//    val displayName: String? = null,
//
//
//    val locationType: String? = null,
//
//
//    val uniqueId: String? = null
//)
//
//
//data class EntityStart(
//
//
//    val dateTime: String? = null,
//
//    val timeZone: String? = null
//)
//
//
//data class EntityEnd(
//
//
//    val dateTime: String? = null,
//
//    val timeZone: String? = null
//)
//
//
//data class EntityLocations(
//
//
//    val uniqueIdType: String? = null,
//
//
//    val displayName: String? = null,
//
//    val locationType: String? = null
//)
//
//
//data class EntityOrganizer(
//
//    val emailAddress: EntityEmailAddress? = null
//)
//
//
//data class EntityEmailAddress(
//
//
//    val address: String? = null,
//    val name: String? = null
//)
//
//
//
//data class EntityResponseStatus(
//
//
//    val response: String? = null,
//
//
//    val time: String? = null
//)
//
//
//data class EntityRemovedState(
//
//    val reason:String? =null
//)
//
//
//data class EntityStatus(
//
//
//    val response: String? = null,
//
//    val time: String? = null
//)
//
//
//data class EntityBody(
//
//
//    val contentType: String? = null,
//
//
//    val content: String? = null
//)

fun List<EventItem?>?.toEntities():List<EventEntity>
{


    val attendees:MutableList<Attendee> = mutableListOf()
    this?.forEach {
        it?.attendees?.map { attendeeItem->
            attendees.add(Attendee(attendeesAddress = attendeeItem?.emailAddress?.address,attendeesName =attendeeItem?.emailAddress?.name,
                attendeesType = attendeeItem?.type, attendeesStatusResponse = attendeeItem?.status?.response, attendeesStatusTime =attendeeItem?.status?.time ))
        }
    }
    return this!!.map {
        EventEntity(id = it?.id!!, subject = it?.subject,entityAttendees = attendees, removedReason = it?.removed?.reason,
            locationDisplayName = it?.location?.displayName, locationType = it?.location?.locationType,
            locationUniqueId = it?.location?.uniqueId, locationUniqueIdType = it?.location?.uniqueIdType,
            startDateTime = it?.start?.dateTime, startTimeZone = it?.start?.timeZone, endDateTime = it?.end?.dateTime,
            endTimeZone = it?.end?.timeZone, organizerAddress = it?.organizer?.emailAddress?.address,
            organizerName =it?.organizer?.emailAddress?.name, bodyPreview = it?.bodyPreview, entityResponse = it?.responseStatus?.response,
            entityResponseTime = it?.responseStatus?.time, isCancelled = it?.isCancelled)
    }
}


