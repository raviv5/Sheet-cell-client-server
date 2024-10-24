package engine.permissions.impl;

import dto.enums.PermissionType;
import dto.enums.Status;
import engine.permissions.api.PermissionManager;
import engine.permissions.request.Request;

import java.util.*;

public class PermissionManagerImpl implements PermissionManager {
    private final String owner;
    private final Set<String> readers;
    private final Set<String> writers;
    private final Set<Request> requestsHistory;

    public PermissionManagerImpl(String owner) {
        this.owner = owner;
        this.readers = new HashSet<>();
        this.writers = new HashSet<>();
        this.requestsHistory = new HashSet<>();
    }

    public static PermissionManagerImpl create(String owner) {
        return new PermissionManagerImpl(owner);
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public Set<String> getReaders() {
        return Collections.unmodifiableSet(readers);
    }

    @Override
    public Set<String> getWriters() {
        return Collections.unmodifiableSet(writers);
    }

    @Override
    public Set<Request> getRequestsHistory() {
        return Collections.unmodifiableSet(requestsHistory);
    }

    @Override
    public void addRequest(String requesterName, PermissionType permissionType) {
        Request pendingRequest = new Request(requesterName, permissionType, Status.PENDING);
        Request comfirmedRequest = new Request(requesterName, permissionType, Status.CONFIRMED);

        if (requestsHistory.contains(comfirmedRequest)) {
            throw new RuntimeException("Permission already confirmed.");
        } else if (requestsHistory.contains(pendingRequest)) {
            throw new RuntimeException("Request already pending.");
        }

        requestsHistory.add(pendingRequest);
    }

    @Override
    public void confirmRequest(String requesterName, PermissionType permissionType) {
        Request pendingRequest = new Request(requesterName, permissionType, Status.PENDING);
        Request confirmedRequest = new Request(requesterName, permissionType, Status.CONFIRMED);

        if (!requestsHistory.contains(pendingRequest)) {
            throw new RuntimeException("Cannot find pending request for this requester.");
        }

        requestsHistory.remove(pendingRequest);
        requestsHistory.add(confirmedRequest);

        if (permissionType == PermissionType.WRITER) {
            writers.add(requesterName);
        } else if (permissionType == PermissionType.READER) {
            readers.add(requesterName);
        } else {
            throw new IllegalArgumentException("Unsupported permission type: " + permissionType);
        }
    }

    @Override
    public void denyRequest(String requesterName, PermissionType permissionType) {
        Request pendingRequest = new Request(requesterName, permissionType, Status.PENDING);
        Request deniedRequest = new Request(requesterName, permissionType, Status.DENIED);

        if (!requestsHistory.contains(pendingRequest)) {
            throw new RuntimeException("Cannot find pending request for this requester.");
        }

        requestsHistory.remove(pendingRequest);
        requestsHistory.add(deniedRequest);
    }

    @Override
    public boolean canRead(String reader) {
        return readers.contains(reader) || writers.contains(reader) || owner.equals(reader);
    }

    @Override
    public boolean canWrite(String writer) {
        return writers.contains(writer) || owner.equals(writer);
    }
}
