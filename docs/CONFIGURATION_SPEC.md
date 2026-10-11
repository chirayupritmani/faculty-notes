# Configuration specification: Faculty Notes application node

| Category | Requirement | Value |
|----------|-------------|-------|
| Packages | Java 21 runtime | openjdk-21-jre-headless |
| Packages | HTTP client for checks | curl, ca-certificates |
| Group | Application group | notesapp (system group) |
| User | Application user, no login | notesapp (system user, shell /usr/sbin/nologin, home /opt/faculty-notes) |
| Folder | Application files | /opt/faculty-notes (notesapp, 0755) |
| Folder | Configuration | /etc/faculty-notes (root:notesapp, 0750) |
| Folder | Logs | /var/log/faculty-notes (notesapp, 0755) |
| File | Application | /opt/faculty-notes/faculty-notes.war (0644) |
| File | Environment settings | /etc/faculty-notes/faculty-notes.env (root:notesapp, 0640): PORT, JAVA_OPTS |
| File | Service definition | /etc/systemd/system/faculty-notes.service (0644) |
| Port | Application HTTP port | 8090/tcp |
| Service | systemd unit | faculty-notes: enabled at boot, running, restarted when its files change |
| Check | Health endpoint | http://127.0.0.1:8090/actuator/health returns status UP |

Run: `ansible-playbook -i ansible/inventory.ini ansible/site.yml -K`
