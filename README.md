# HoneyTrap

HoneyTrap is a passive research honeypot for observing automated probes. It exposes deliberately attractive but **inert** fake endpoints, records the request metadata, and serves a live dashboard from the same Spring Boot application.

## Ethics and legal use
Use HoneyTrap only on infrastructure you own or are explicitly authorized to monitor. This is a research/learning honeypot, not an active defense system. Be transparent about monitoring where required by policy or law. Do not use collected data to retaliate, harass, scan back, or attack anyone. Deploying a honeypot on infrastructure you do not own, in a way that could be interpreted as entrapment, or contrary to a hosting provider's terms can have legal and ethical implications. You are responsible for appropriate use and for protecting stored logs.

Source IPs are best-effort: proxies can add `X-Forwarded-For`, and request headers/IP information can be spoofed.

## What it exposes

All trap endpoints are inert. They never read the real filesystem, execute commands, evaluate submitted input, or return real secrets.

| Endpoint | Methods | Behavior |
|---|---|---|
| `/wp-login.php` | GET, POST | Fake WordPress-style login; POST always fails |
| `/wp-admin/**` | GET, POST | Fake admin page; POST always fails |
| `/.env` | GET | Fake placeholder configuration only |
| `/.git/config` | GET | Fake git config using `example.invalid` |
| `/phpmyadmin/**` | GET | Fake phpMyAdmin login |
| `/admin/login` | GET, POST | Fake admin login; POST always fails |
| `/api/v1/users` | GET | Two obviously fake users |
| `/shell.php` | GET, POST | Logs and returns 404; never executes input |
| `/cmd.php` | GET, POST | Logs and returns 404; never executes input |
| common scanner paths | GET | Pattern-based logging and 404 fallback; markers include `.php`, `.asp`, `.aspx`, `wp-`, `xmlrpc.php`, `cgi-bin`, `.env`, `.git/`, and related probe paths |

The trap logger captures timestamp, remote source IP, `X-Forwarded-For`, method, path/query string, bounded request headers, user-agent, and a request body capped at 2 KB. Stored field sizes are bounded to reduce disk/memory abuse.

## Dashboard API

Dashboard endpoints use `/api/stats/**` and `/api/hits/**`. Local development leaves API-key protection disabled. Before a public deployment, set `HONEYTRAP_API_KEY_ENABLED=true` and a strong `HONEYTRAP_API_KEY`; send that value in the `X-HoneyTrap-Api-Key` header. The bait endpoint `/api/v1/users` remains public by design.

- `GET /api/health` — service health check.
- `GET /api/stats/summary` — total hits, last-24-hour hits, unique IPs, most-targeted endpoint, most-active IP.
- `GET /api/stats/timeseries?bucket=hour|day` — UTC hit counts bucketed by hour or day.
- `GET /api/stats/top-ips?limit=10` — top source IPs and counts.
- `GET /api/stats/top-paths?limit=10` — top paths and counts.
- `GET /api/hits?page=0&size=50&ip=&path=` — newest-first paginated hit log with optional filters.
- `GET /api/hits/{id}` — complete detail for one stored hit.

## Retention

H2 stores data in `./data/honeytrap`. The application keeps at most 50,000 hits by default and prunes the oldest records on a scheduled task. Configure with `HONEYTRAP_RETENTION_MAX_HITS` and `HONEYTRAP_RETENTION_PRUNE_DELAY_MS`.

## Run locally

Requirements: Java 21 and Maven.

```bash
./mvnw spring-boot:run
```

Then open `http://localhost:8080/` for the dashboard or `http://localhost:8080/api/health` for the health check.

To run tests:

```bash
./mvnw test
```

The repository includes `mvnw`/`mvnw.cmd` entry points; when Maven is installed they delegate to the local Maven installation.

## Docker

Build the application first, then build the image:

```bash
./mvnw clean package

docker build -t honeytrap .
docker run --rm -p 8080:8080 -v "$PWD/data:/app/data" honeytrap
```

On Windows PowerShell, use `-v "${PWD}/data:/app/data"`.

## Security design

Trap responses are static or generic. User-controlled values are never reflected into fake HTML. The dashboard renders log values with DOM `textContent`, not raw HTML, so hostile paths, headers, user-agents, and bodies are treated as untrusted data. Request bodies are capped at 2 KB and header/path values are bounded before persistence.

The dashboard is intentionally passive: there is no IP banning/blocking UI, no retaliation workflow, and no alerting. WebSockets are out of scope for the MVP; polling is used and a future push implementation can replace it. GeoIP is also intentionally not implemented to avoid an external network dependency; a future version could add an optional provider. Alerting (email/Slack) is a future TODO.
