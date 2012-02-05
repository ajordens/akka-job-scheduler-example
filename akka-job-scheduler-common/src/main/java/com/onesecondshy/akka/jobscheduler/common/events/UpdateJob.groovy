package com.onesecondshy.akka.jobscheduler.common.events

/**
 * @author Adam Jordens (adam@jordens.org)
 */
class UpdateJob extends Event {
    private static final long serialVersionUID = -1354942905395394545L;

    String jobId
    String status
    Serializable results

    UpdateJob(String jobId, String status, Serializable results) {
        this.jobId = jobId
        this.status = status
        this.results = results
    }

    @Override
    public String toString() {
        return "UpdateJob{" +
                "jobId='" + jobId + '\'' +
                ", status='" + status + '\'' +
                ", results=" + results +
                '}';
    }


}
