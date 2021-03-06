package com.eslam.csp1401_test

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Events(

	@field:SerializedName("@odata.context")
	val odataContext: String? = null,

	@field:SerializedName("value")
	val value: List<EventItem?>? = null
) : Parcelable

@Parcelize
data class Status(

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("time")
	val time: String? = null
) : Parcelable

@Parcelize
data class Body(

	@field:SerializedName("contentType")
	val contentType: String? = null,

	@field:SerializedName("content")
	val content: String? = null
) : Parcelable

@Parcelize
data class EventItem(

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("attendees")
	val attendees: List<AttendeesItem?>? = null,


	@field:SerializedName("@removed")
	val removed: RemovedState? = null,

	@field:SerializedName("organizer")
	val organizer: Organizer? = null,

	@field:SerializedName("@odata.etag")
	val odataEtag: String? = null,

	@field:SerializedName("start")
	val start: Start? = null,

	@field:SerializedName("bodyPreview")
	val bodyPreview: String? = null,

	@field:SerializedName("responseStatus")
	val responseStatus: ResponseStatus? = null,

	@field:SerializedName("end")
	val end: End? = null,

	@field:SerializedName("location")
	val location: Location? = null,

	@field:SerializedName("locations")
	val locations: List<LocationsItem?>? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("body")
	val body: Body? = null,

	@field:SerializedName("isCancelled")
	val isCancelled: Boolean? = null,

	@field:SerializedName("allowNewTimeProposals")
      val allowNewTimeProposals:Boolean? = true
) : Parcelable

@Parcelize
data class AttendeesItem(

	@field:SerializedName("emailAddress")
	val emailAddress: EmailAddress? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("status")
	val status: Status? = null
) : Parcelable

@Parcelize
data class Location(

	@field:SerializedName("uniqueIdType")
	val uniqueIdType: String? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("locationType")
	val locationType: String? = null,

	@field:SerializedName("uniqueId")
	val uniqueId: String? = null
) : Parcelable

@Parcelize
data class Start(

	@field:SerializedName("dateTime")
	val dateTime: String? = null,

	@field:SerializedName("timeZone")
	val timeZone: String? = null
) : Parcelable

@Parcelize
data class End(

	@field:SerializedName("dateTime")
	val dateTime: String? = null,

	@field:SerializedName("timeZone")
	val timeZone: String? = null
) : Parcelable

@Parcelize
data class LocationsItem(

	@field:SerializedName("uniqueIdType")
	val uniqueIdType: String? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("locationType")
	val locationType: String? = null
) : Parcelable

@Parcelize
data class Organizer(

	@field:SerializedName("emailAddress")
	val emailAddress: EmailAddress? = null
) : Parcelable

@Parcelize
data class EmailAddress(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("name")
	val name: String? = null
) : Parcelable


@Parcelize
data class ResponseStatus(

	@field:SerializedName("response")
	val response: String? = null,

	@field:SerializedName("time")
	val time: String? = null
) : Parcelable

@Parcelize
  data class RemovedState(
	@field:SerializedName("reason")
	  val reason:String? =null
  ): Parcelable