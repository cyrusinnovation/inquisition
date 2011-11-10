package com.trailmagic.jumper.web.model

import com.cyrusinnovation.inquisition.response.Response
import reflect.BeanProperty
import org.hibernate.validator.constraints.NotEmpty

class ResponseFormData {
    def this(response: Response) = {
        this ()
        title = response.title
        body = response.body
        id = response.id.getOrElse("")
    }

    @NotEmpty(message = "Response title cannot be empty")
    @BeanProperty var title = ""
    @NotEmpty(message = "Response body cannot be empty")
    @BeanProperty var body = ""
    @BeanProperty var id = ""

    def toResponse: Response = {
        val responseId = id match {
            case (x) if !x.isEmpty => Some(x)
            case _ => None
        }
        Response(responseId, title, "tester", body)
    }
}