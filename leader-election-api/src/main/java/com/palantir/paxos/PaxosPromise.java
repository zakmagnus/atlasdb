/**
 * Copyright 2015 Palantir Technologies
 *
 * Licensed under the BSD-3 License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.paxos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.palantir.common.annotation.Immutable;

/**
 * A promise to not accept new proposals less than promisedID.
 *
 * @author rullman
 */
@Immutable
public class PaxosPromise implements Comparable<PaxosPromise>, PaxosResponse {
    private static final long serialVersionUID = 1L;

    final boolean ack;

    @Nonnull final PaxosProposalId promisedId;
    @Nullable final PaxosProposalId lastAcceptedId;
    @Nullable final PaxosValue lastAcceptedValue;

    public PaxosPromise(PaxosProposalId promisedId) {
        ack = false;
        this.promisedId = Preconditions.checkNotNull(promisedId);
        lastAcceptedId = null;
        lastAcceptedValue = null;
    }

    public PaxosPromise(PaxosProposalId promisedId,
                        PaxosProposalId lastAcceptedId,
                        PaxosValue val) {
        ack = true;
        this.promisedId = Preconditions.checkNotNull(promisedId);
        this.lastAcceptedId = lastAcceptedId;
        this.lastAcceptedValue = val;
    }

    @JsonCreator
    public static PaxosPromise create(@JsonProperty("successful") boolean ack,
                                      @JsonProperty("promisedId") PaxosProposalId promisedId,
                                      @JsonProperty("lastAcceptedId") PaxosProposalId lastAcceptedId,
                                      @JsonProperty("lastAcceptedValue") PaxosValue val) {
        if (ack) {
            return new PaxosPromise(promisedId, lastAcceptedId, val);
        } else {
            return new PaxosPromise(promisedId);
        }
    }

    @Override
    // XXX Contrary to common wisdom, this is NOT consistent with equals().
    public int compareTo(PaxosPromise o) {
        // nulls are less than non-nulls so nacks are less than acks
        return new CompareToBuilder().append(lastAcceptedId, o.lastAcceptedId).toComparison();
    }

    @Override
    public boolean isSuccessful() {
        return ack;
    }

    public PaxosProposalId getPromisedId() {
        return promisedId;
    }

    public PaxosProposalId getLastAcceptedId() {
        return lastAcceptedId;
    }

    public PaxosValue getLastAcceptedValue() {
        return lastAcceptedValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (ack ? 1231 : 1237);
        result = prime * result
                + ((lastAcceptedId == null) ? 0 : lastAcceptedId.hashCode());
        result = prime
                * result
                + ((lastAcceptedValue == null) ? 0
                : lastAcceptedValue.hashCode());
        result = prime * result
                + ((promisedId == null) ? 0 : promisedId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PaxosPromise other = (PaxosPromise) obj;
        if (ack != other.ack) {
            return false;
        }
        if (lastAcceptedId == null) {
            if (other.lastAcceptedId != null) {
                return false;
            }
        } else if (!lastAcceptedId.equals(other.lastAcceptedId)) {
            return false;
        }
        if (lastAcceptedValue == null) {
            if (other.lastAcceptedValue != null) {
                return false;
            }
        } else if (!lastAcceptedValue.equals(other.lastAcceptedValue)) {
            return false;
        }
        if (promisedId == null) {
            if (other.promisedId != null) {
                return false;
            }
        } else if (!promisedId.equals(other.promisedId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PaxosPromise [ack=" + ack + ", promisedId=" + promisedId
                + ", lastAcceptedId=" + lastAcceptedId + ", lastAcceptedValue="
                + lastAcceptedValue + "]";
    }
}
