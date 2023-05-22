package kh.com.kshrd.docengine.repository.provider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DocumentSqlProvider {
    public String getDocumentsByWorkspaceAndTags(Map<String, Object> parameters) {
        UUID workspaceId = (UUID) parameters.get("workspaceId");
        List<String> tags = (List<String>) parameters.get("tags");

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT d.document_id, title, status, created_date, page_id, d.workspace_id ")
                .append("FROM tags ")
                .append("INNER JOIN tag_document td ON tags.tag_id = td.tag_id ")
                .append("INNER JOIN documents d ON d.document_id = td.document_id ")
                .append("WHERE d.workspace_id = #{workspaceId} ")
                .append("AND tag_name IN ");

        StringBuilder tagsBuilder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            String tagParam = "tag" + i;

            if (i > 0) {
                tagsBuilder.append(",");
            }

            tagsBuilder.append("#{").append(tagParam).append("}");
            parameters.put(tagParam, tags.get(i));
        }

        sqlBuilder.append("(").append(tagsBuilder).append(")");

        return sqlBuilder.toString();
    }
}

