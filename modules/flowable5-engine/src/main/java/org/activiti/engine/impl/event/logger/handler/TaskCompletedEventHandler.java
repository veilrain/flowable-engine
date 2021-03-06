/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl.event.logger.handler;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.EventLogEntryEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.flowable.engine.delegate.event.FlowableEntityWithVariablesEvent;

/**
 * @author Joram Barrez
 */
public class TaskCompletedEventHandler extends AbstractTaskEventHandler {

    @Override
    public EventLogEntryEntity generateEventLogEntry(CommandContext commandContext) {

        FlowableEntityWithVariablesEvent eventWithVariables = (FlowableEntityWithVariablesEvent) event;
        TaskEntity task = (TaskEntity) eventWithVariables.getEntity();
        Map<String, Object> data = handleCommonTaskFields(task);

        long duration = timeStamp.getTime() - task.getCreateTime().getTime();
        putInMapIfNotNull(data, Fields.DURATION, duration);

        if (eventWithVariables.getVariables() != null && !eventWithVariables.getVariables().isEmpty()) {
            Map<String, Object> variableMap = new HashMap<String, Object>();
            for (Object variableName : eventWithVariables.getVariables().keySet()) {
                putInMapIfNotNull(variableMap, (String) variableName, eventWithVariables.getVariables().get(variableName));
            }
            if (eventWithVariables.isLocalScope()) {
                putInMapIfNotNull(data, Fields.LOCAL_VARIABLES, variableMap);
            } else {
                putInMapIfNotNull(data, Fields.VARIABLES, variableMap);
            }
        }

        return createEventLogEntry(task.getProcessDefinitionId(), task.getProcessInstanceId(), task.getExecutionId(), task.getId(), data);
    }

}
