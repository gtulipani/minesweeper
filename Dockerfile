FROM openjdk:11-jre-slim

RUN addgroup --gid 1000 minesweeper && \
    adduser --uid 1000 --gecos '' --gid 1000 minesweeper

ENV WORKSPACE=/workspace

RUN mkdir -p $WORKSPACE && chown -R minesweeper:minesweeper $WORKSPACE

USER minesweeper

WORKDIR $WORKSPACE

ENV XMS="-Xms256m"
ENV XMX="-Xmx256m"
ENV METASPACE="-XX:MaxMetaspaceSize=96m"
ENV ILLEGAL_ACCESS_PERMIT="--illegal-access=permit"
ENV BASE_JAVA_OPTS="-XX:NativeMemoryTracking=summary"

ENV JAVA_OPTS="$BASE_JAVA_OPTS $XMS $XMX $METASPACE $ILLEGAL_ACCESS_PERMIT"
ENV EXTRA_ARGS=""

ARG ARTIFACT_VERSION=latest
ADD --chown=minesweeper:minesweeper build/libs/minesweeper-${ARTIFACT_VERSION}.jar $WORKSPACE/minesweeper.jar
ADD --chown=minesweeper:minesweeperservice entrypoint.sh /entrypoint.sh

# Expose endpoints on port 8090
EXPOSE 8090

ENTRYPOINT ["/entrypoint.sh"]
