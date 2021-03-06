package org.seeko

class ProjectController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [projectInstanceList: Project.list(params), projectInstanceTotal: Project.count()]
    }

    def create() {
        [projectInstance: new Project(params)]
    }

    def save() {
        def projectInstance = new Project(params)
        if (!projectInstance.save(flush: true)) {
            render projectInstance.errors.allErrors.collect {
                message(error: it, encodeAs: 'HTML')
            }
            return
        }
        render message(code: 'seeko.messages.create.project.success')
    }


    def edit(Long id) {
        def projectInstance = Project.get(id)
        if (!projectInstance) {
            //TODO: Fish, update the default message code
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), id])
            redirect(action: "list")
            return
        }

        [projectInstance: projectInstance]
    }

    def update(Long id, Long version) {
        def projectInstance = Project.get(id)
        if (!projectInstance) {

            //TODO: Fish, update the default message code
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (projectInstance.version > version) {
                //TODO: Fish, update the default message code
                projectInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'project.label', default: 'Project')] as Object[],
                        "Another user has updated this Project while you were editing")

                //TODO: Fish, update to render JSON or HTML
                render(view: "edit", model: [projectInstance: projectInstance])
                return
            }
        }

        projectInstance.properties = params

        if (!projectInstance.save(flush: true)) {
            //TODO: Fish, update to render JSON or HTML
            render(view: "edit", model: [projectInstance: projectInstance])
            return
        }

        //TODO: Fish, update the default message code
        flash.message = message(code: 'default.updated.message', args: [message(code: 'project.label', default: 'Project'), projectInstance.id])
        redirect(action: "show", id: projectInstance.id)
    }

}
